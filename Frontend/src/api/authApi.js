import axios from "axios";

const BASE_URL = "http://localhost:8080/gateway/auth";

export async function register({ username, email, password }) {
  const res = await axios.post(`${BASE_URL}/register`, {
    username,
    email,
    password,
  });
  return res.data;
}

export async function verifyEmail(token) {
  const res = await axios.get(`${BASE_URL}/verify`, {
    params: { token },
  });
  return res.data;
}

export async function login({ email, password }) {
  const res = await axios.post(`${BASE_URL}/login`, {
    email,
    password,
  });

  return res.data;
}
