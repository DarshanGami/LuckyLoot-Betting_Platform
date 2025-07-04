import { motion } from 'framer-motion'
import { Dice1, Heart, Spade, Diamond } from 'lucide-react'

function CasinoSection() {
  const casinoGames = [
    {
      id: 1,
      name: 'Blackjack',
      type: 'Card Game',
      players: 234,
      minBet: 5,
      maxBet: 1000,
      icon: Spade
    },
    {
      id: 2,
      name: 'Roulette',
      type: 'Table Game',
      players: 156,
      minBet: 1,
      maxBet: 500,
      icon: Heart
    },
    {
      id: 3,
      name: 'Poker',
      type: 'Card Game',
      players: 89,
      minBet: 10,
      maxBet: 2000,
      icon: Diamond
    },
    {
      id: 4,
      name: 'Slots',
      type: 'Slot Machine',
      players: 567,
      minBet: 0.1,
      maxBet: 100,
      icon: Dice1
    }
  ]

  const slotGames = [
    { id: 1, name: 'Mega Fortune', jackpot: '$2,456,789' },
    { id: 2, name: 'Starburst', jackpot: '$156,234' },
    { id: 3, name: 'Book of Dead', jackpot: '$89,567' },
    { id: 4, name: 'Gonzo\'s Quest', jackpot: '$234,890' }
  ]

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-white mb-2">Casino Games</h2>
        <p className="text-gray-400">Live casino and slot machines</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
        {casinoGames.map((game, index) => {
          const Icon = game.icon
          return (
            <motion.div
              key={game.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              whileHover={{ scale: 1.02 }}
              className="bg-gray-800 border border-gray-700 rounded-lg p-6 cursor-pointer hover:border-bright-red transition-colors"
            >
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="p-2 bg-bright-red rounded-lg">
                    <Icon className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h3 className="text-lg font-semibold text-white">{game.name}</h3>
                    <p className="text-gray-400 text-sm">{game.type}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-green-400 text-sm">{game.players} playing</p>
                </div>
              </div>

              <div className="flex items-center justify-between">
                <div>
                  <p className="text-gray-400 text-sm">Bet Range</p>
                  <p className="text-white">${game.minBet} - ${game.maxBet}</p>
                </div>
                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  className="bg-bright-red hover:bg-red-600 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                >
                  Play Now
                </motion.button>
              </div>
            </motion.div>
          )
        })}
      </div>

      <div className="bg-gray-800 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-white mb-4">Progressive Jackpots</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {slotGames.map((slot) => (
            <motion.div
              key={slot.id}
              whileHover={{ scale: 1.02 }}
              className="bg-gray-700 p-4 rounded-lg cursor-pointer hover:bg-gray-600 transition-colors"
            >
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-medium text-white">{slot.name}</h4>
                  <p className="text-gray-400 text-sm">Progressive Jackpot</p>
                </div>
                <div className="text-right">
                  <p className="text-yellow-400 font-bold">{slot.jackpot}</p>
                  <motion.button
                    whileHover={{ scale: 1.05 }}
                    className="text-bright-red text-sm hover:text-red-400 transition-colors"
                  >
                    Play
                  </motion.button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </motion.div>
  )
}

export default CasinoSection