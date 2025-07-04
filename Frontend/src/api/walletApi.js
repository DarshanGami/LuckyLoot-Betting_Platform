import axios from "axios";

const BASE_URL = "http://localhost:8080/gateway/payment";
const BALANCE_URL = "http://localhost:8080/gateway/wallet/balance";

export async function createDepositOrder({ userId, amount }, token) {
//   console.log("Wallet Token:", token);
  const res = await axios.post(
    `${BASE_URL}/deposit`,
    { userId, amount },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return res.data;
}

export async function confirmDeposit({ userId, amount, orderId }, token) {
  const res = await axios.post(
    `${BASE_URL}/confirm`,
    { userId, amount, orderId },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return res.data;
}

export async function withdraw({ amount, bankDetails }, token) {
  const res = await axios.post(
    `${BASE_URL}/withdraw`,
    { amount, bankDetails },
    {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    }
  );
  return res.data;
}

export async function getBalance() {
  const token = localStorage.getItem("token");
  try {
    const res = await axios.get(BALANCE_URL, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    // console.log("Balance API raw response:", res.data);
    // If the response is a number directly
    if (typeof res.data === "number") {
      return res.data;
    }
    // If the response is an object with .balance
    if (typeof res.data.balance === "number") {
      return res.data.balance;
    }
    if (typeof res.data.balance === "string" && !isNaN(Number(res.data.balance))) {
      return Number(res.data.balance);
    }
    return 0;
  } catch (err) {
    console.error("Failed to fetch balance:", err);
    throw err;
  }
}
