import axios from "axios";

const BASE_URL = "http://localhost:8080/gateway/bet";

export async function placeBet({ matchId, team, oddAtBet, amount }) {
  const token = localStorage.getItem("token");
  try {
    const res = await axios.post(
      `${BASE_URL}/place`,
      { matchId, team, oddAtBet, amount },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return res.data;
  } catch (err) {
    throw err;
  }
}

export async function getUserBets() {
  const token = localStorage.getItem("token");
  try {
    const res = await axios.get(`${BASE_URL}/my-bets`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return res.data;
  } catch (err) {
    throw err;
  }
}

export async function cashoutBet(betId) {
  const token = localStorage.getItem("token");
  try {
    const res = await axios.post(
      `${BASE_URL}/cashout?betId=${betId}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return res.data;
  } catch (err) {
    throw err;
  }
} 