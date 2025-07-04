import axios from "axios";

const BASE_URL = "http://localhost:8080/gateway/match";

export async function getAllMatches() {
  const token = localStorage.getItem("token");
  try {
    const res = await axios.get(`${BASE_URL}/all-matches`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return res.data;
  } catch (err) {
    throw err;
  }
} 