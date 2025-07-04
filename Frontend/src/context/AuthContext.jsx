import { createContext, useContext, useEffect, useState } from "react";
import * as authApi from "../api/authApi";
import { getBalance } from '../api/walletApi';
import { getUserBets } from '../api/betApi';

// Create the context
const AuthContext = createContext();

// Custom hook to use AuthContext
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}

// Provider Component
export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [balance, setBalance] = useState(null);
  const [balanceLoading, setBalanceLoading] = useState(false);
  const [balanceError, setBalanceError] = useState(null);

  const [userBets, setUserBets] = useState([]);
  const [userBetsLoading, setUserBetsLoading] = useState(false);
  const [userBetsError, setUserBetsError] = useState(null);

  // Always use backend for balance
  const refreshBalance = async () => {
    setBalanceLoading(true);
    setBalanceError(null);
    try {
      const bal = await getBalance();
      setBalance(bal);
      // console.log('Balance response:', bal);
    } catch (err) {
      setBalanceError("Couldn't load balance.");
    } finally {
      setBalanceLoading(false);
    }
  };
  useEffect(() => {
    if (token) {
      refreshBalance();
    }
  }, [token]);

  useEffect(() => {
  const savedToken = localStorage.getItem("token");
  const savedUser = localStorage.getItem("user");

  if (savedToken) {
    setToken(savedToken);
    setIsAuthenticated(true);
  }
  if (savedUser) {
    setUser(JSON.parse(savedUser));
  }
}, []);

// Register user
const register = async (username, email, password) => {
  try {
    const data = await authApi.register({ username, email, password });
    console.log("Register success:", data);
    return { success: true, data };
  } catch (err) {
    console.error(err);
    return { success: false, error: err.response?.data || err.message };
  }
};

// Verify email token
const verify = async (token) => {
  try {
    const data = await authApi.verifyEmail(token);
    console.log("Email verified:", data);
    return { success: true, data };
  } catch (err) {
    console.error(err);
    return { success: false, error: err.response?.data || err.message };
  }
};

// Login user and save token + user info
const login = async (email, password) => {
  try {
    const data = await authApi.login({ email, password });
    console.log("Login API response:", data);

    let token = null;
    let user = null;

    if (typeof data === "string") {
      token = data;
    } else if (data && data.token) {
      token = data.token;
      user = data.user;
    }

    if (token) {
      localStorage.setItem("token", token);
      setToken(token);
      setIsAuthenticated(true);
    }

    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
      setUser(user);
    }

    return { success: !!token, data };
  } catch (err) {
    console.error(err);
    return { success: false, error: err.response?.data || err.message };
  }
};

// Logout user and clear token + user info
const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  setToken(null);
  setUser(null);
  setIsAuthenticated(false);
};

  // Place a new bet (no local balance update)
  const placeBet = (bet) => {
    const newBet = {
      id: Date.now(),
      ...bet,
      status: "active",
      canCashout: Math.random() > 0.5,
    };
    setUserBets((prev) => [newBet, ...prev]);
  };

  // Cash out a bet (no local balance update)
  const cashoutBet = (betId) => {
    const bet = userBets.find((b) => b.id === betId);
    if (!bet) return;
    setUserBets((prev) =>
      prev.map((b) =>
        b.id === betId
          ? { ...b, status: "cashed_out", payout: b.cashoutValue }
          : b
      )
    );
  };

  // Update wallet balance (no-op, for compatibility)
  const updateBalance = () => {};

  const fetchUserBets = async () => {
    setUserBetsLoading(true);
    setUserBetsError(null);
    try {
      const bets = await getUserBets();
      setUserBets(bets);
    } catch (err) {
      setUserBetsError('Failed to load bets.');
    } finally {
      setUserBetsLoading(false);
    }
  };

  // Fetch bets after login
  useEffect(() => {
    if (isAuthenticated && token) {
      fetchUserBets();
    }
  }, [isAuthenticated, token]);

  const value = {
    isAuthenticated,
    user,
    token,
    balance,
    setBalance,
    balanceLoading,
    balanceError,
    userBets,
    setUserBets,
    userBetsLoading,
    userBetsError,
    fetchUserBets,
    login,
    register,
    verify,
    logout,
    placeBet,
    cashoutBet,
    updateBalance,
    refreshBalance,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
