const { chromium } = require('playwright');
const { Kafka } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'odds-engine',
  brokers: ['localhost:9092'],
});

const producer = kafka.producer();
const topic = 'cricket-odds-update';
const sleep = (ms) => new Promise((res) => setTimeout(res, ms));

// Cache to track last sent state
let lastSentData = new Map();

async function startOddsEngine() {
  await producer.connect();
  console.log('🚀 Odds Engine started and connected to Kafka!');

  while (true) {
    try {
      const browser = await chromium.launch({ headless: true });
      const context = await browser.newContext();
      const page = await context.newPage();

      await page.goto('https://www.flashscore.com/cricket/', { waitUntil: 'domcontentloaded' });
      await page.waitForTimeout(4000);

      const allMatches = await page.$$('.event__match--live');
      let allData = [];

      for (const match of allMatches) {
        const team1 = await match.$eval('.event__participant--home', el => el.textContent.trim()).catch(() => 'N/A');
        const team2 = await match.$eval('.event__participant--away', el => el.textContent.trim()).catch(() => 'N/A');
        const score1 = await match.$eval('.event__score--home', el => el.textContent.trim()).catch(() => '-');
        const score2 = await match.$eval('.event__score--away', el => el.textContent.trim()).catch(() => '-');
        const status = await match.$eval('.event__stage', el => el.textContent.trim()).catch(() => 'Live');

        const iconBatting1 = await match.$('svg.icon--serveHome use');
        const iconBatting2 = await match.$('svg.icon--serveAway use');
        const href1 = iconBatting1 ? await iconBatting1.getAttribute('xlink:href') : '';
        const href2 = iconBatting2 ? await iconBatting2.getAttribute('xlink:href') : '';
        const isBatting1 = href1?.includes('cricket-bat');
        const isBatting2 = href2?.includes('cricket-bat');

        allData.push({
          team1, team2,
          score1, score2,
          status,
          isBatting1, isBatting2,
          odd1: 'N/A',
          odd2: 'N/A'
        });
      }

      const oddsTab = await page.locator('.filters__tab').filter({ hasText: 'Odds' }).first();
      await oddsTab.click();
      await sleep(2500);

      const oddsMatches = await page.$$('.event__match--live');
      for (const match of oddsMatches) {
        const team1 = await match.$eval('.event__participant--home', el => el.textContent.trim()).catch(() => 'N/A');
        const team2 = await match.$eval('.event__participant--away', el => el.textContent.trim()).catch(() => 'N/A');
        const odd1 = await match.$eval('.event__odd--odd1 span', el => el.textContent.trim()).catch(() => '❌');
        const odd2 = await match.$eval('.event__odd--odd2 span', el => el.textContent.trim()).catch(() => '❌');

        const existing = allData.find(m => m.team1 === team1 && m.team2 === team2);
        if (existing) {
          existing.odd1 = odd1;
          existing.odd2 = odd2;
        }
      }

      const currentKeys = new Set();

      for (const match of allData) {
        const key = `${match.team1}_vs_${match.team2}`;
        currentKeys.add(key);
        const newPayload = JSON.stringify(match);

        if (!lastSentData.has(key) || lastSentData.get(key) !== newPayload) {
          console.log(`📤 [UPDATED] Sending to Kafka:\n${newPayload}`);
          await producer.send({
            topic,
            messages: [{ key, value: newPayload }]
          });
          lastSentData.set(key, newPayload);
        }
      }

      // 🔻 Detect ended matches and send deletion
      for (const oldKey of lastSentData.keys()) {
        if (!currentKeys.has(oldKey)) {
          const [team1, , team2] = oldKey.split('_');
          const deletePayload = JSON.stringify({ team1, team2, deleted: true });

          console.log(`❌ [REMOVED] Sending to Kafka:\n${deletePayload}`);
          await producer.send({
            topic,
            messages: [{ key: oldKey, value: deletePayload }]
          });

          lastSentData.delete(oldKey);
        }
      }

      await browser.close();
    } catch (err) {
      console.error('❌ Error in odds engine loop:', err.message);
    }

    await sleep(15000); // Wait before next run
  }
}

startOddsEngine();
