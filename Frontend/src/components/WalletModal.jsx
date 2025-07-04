import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuth } from '../context/AuthContext'
import { X, CreditCard, DollarSign, Wallet, AlertCircle } from 'lucide-react'
import * as walletApi from "../api/walletApi";

function WalletModal({ action, onClose }) {
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [bankDetails, setBankDetails] = useState({
    accountNumber: "",
    ifsc: "",
    holderName: "",
  });

  const { user, token, balance, balanceLoading, balanceError, refreshBalance } = useAuth();

  const quickAmounts = [10, 50, 100, 500, 1000, 5000];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess(false);

    const amountNum = parseFloat(amount);
    if (!amountNum || amountNum <= 0) {
      setError("Please enter a valid amount");
      return;
    }

    if (action === "withdraw" && amountNum > balance) {
      setError("Insufficient funds");
      return;
    }

    if (action === "deposit" && amountNum > 10000) {
      setError("Maximum deposit limit is $10,000");
      return;
    }

    if (action === "withdraw" && amountNum < 10) {
      setError("Minimum withdrawal amount is $10");
      return;
    }

    setLoading(true);

    try {
      if (action === "deposit") {
        const orderData = await walletApi.createDepositOrder(
          {
            userId: user?.userId,
            amount: amountNum,
          },
          token
        );

        await walletApi.confirmDeposit(
          {
            userId: user?.userId,
            amount: amountNum,
            orderId: orderData.orderId,
          },
          token
        );

        setSuccess(true);
        await refreshBalance();
      }

      if (action === "withdraw") {
        await walletApi.withdraw(
          {
            amount: amountNum,
            bankDetails,
          },
          token
        );

        setSuccess(true);
        await refreshBalance();
      }

      setTimeout(() => {
        onClose();
      }, 2000);
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || "Something went wrong. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
        onClick={onClose}
      >
        <motion.div
          initial={{ scale: 0.9, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          exit={{ scale: 0.9, opacity: 0 }}
          className="bg-gray-800 rounded-lg max-w-md w-full p-6"
          onClick={(e) => e.stopPropagation()}
        >
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center space-x-3">
              {action === 'deposit' ? (
                <div className="p-2 bg-green-600 rounded-lg">
                  <DollarSign className="w-6 h-6 text-white" />
                </div>
              ) : (
                <div className="p-2 bg-orange-600 rounded-lg">
                  <Wallet className="w-6 h-6 text-white" />
                </div>
              )}
              <div>
                <h2 className="text-xl font-bold text-white">
                  {action === 'deposit' ? 'Deposit Funds' : 'Withdraw Funds'}
                </h2>
                <p className="text-sm text-gray-400">
                  Current Balance: {balanceLoading ? (
                    <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin inline-block"></span>
                  ) : balanceError ? (
                    <span className="text-red-400 text-sm">Couldn't load</span>
                  ) : (
                    <span className="text-green-400 font-bold">${typeof balance === 'number' && !isNaN(balance) ? balance.toFixed(2) : '0.00'}</span>
                  )}
                </p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-white transition-colors"
            >
              <X className="w-6 h-6" />
            </button>
          </div>

          {success ? (
            <motion.div
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              className="text-center py-8"
            >
              <div className="w-16 h-16 bg-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ delay: 0.2 }}
                >
                  <DollarSign className="w-8 h-8 text-white" />
                </motion.div>
              </div>
              <h3 className="text-lg font-semibold text-white mb-2">
                {action === 'deposit' ? 'Deposit Successful!' : 'Withdrawal Successful!'}
              </h3>
              <p className="text-gray-400">
                ${amount} has been {action === 'deposit' ? 'added to' : 'withdrawn from'} your account
              </p>
            </motion.div>
          ) : (
            <form onSubmit={handleSubmit}>
              {/* Amount Input */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-400 mb-2">
                  Amount ($)
                </label>
                <div className="relative">
                  <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="w-full pl-10 pr-4 py-3 bg-gray-700 border border-gray-600 rounded-lg focus:ring-2 focus:ring-bright-red focus:border-transparent text-white placeholder-gray-400"
                    placeholder="Enter amount"
                    min="0"
                    step="0.01"
                    disabled={loading}
                  />
                </div>
              </div>

              {/* Quick Amount Buttons */}
              <div className="mb-6">
                <p className="text-sm font-medium text-gray-400 mb-3">Quick Select</p>
                <div className="grid grid-cols-3 gap-2">
                  {quickAmounts.map((quickAmount) => (
                    <motion.button
                      key={quickAmount}
                      type="button"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                      onClick={() => setAmount(quickAmount.toString())}
                      className="py-2 px-3 bg-gray-700 hover:bg-gray-600 text-white rounded-lg text-sm font-medium transition-colors"
                      disabled={loading}
                    >
                      ${quickAmount}
                    </motion.button>
                  ))}
                </div>
              </div>

              {/* Error Message */}
              {error && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="mb-4 p-3 bg-red-900 border border-red-700 rounded-lg flex items-center space-x-2"
                >
                  <AlertCircle className="w-4 h-4 text-red-400" />
                  <span className="text-sm text-red-400">{error}</span>
                </motion.div>
              )}

              {/* Payment Method */}
              {action === 'deposit' && (
                <div className="mb-6">
                  <p className="text-sm font-medium text-gray-400 mb-3">Payment Method</p>
                  <div className="bg-gray-700 border border-gray-600 rounded-lg p-4 flex items-center space-x-3">
                    <CreditCard className="w-5 h-5 text-gray-400" />
                    <div>
                      <p className="text-sm font-medium text-white">Credit/Debit Card</p>
                      <p className="text-xs text-gray-400">**** **** **** 1234</p>
                    </div>
                  </div>
                </div>
              )}

              {/* Limits */}
              <div className="mb-6 p-3 bg-gray-700 rounded-lg">
                <p className="text-xs text-gray-400 mb-1">
                  {action === 'deposit' ? 'Deposit Limits' : 'Withdrawal Limits'}
                </p>
                <p className="text-xs text-gray-300">
                  {action === 'deposit'
                    ? 'Min: $1 | Max: $10,000 per transaction'
                    : 'Min: $10 | Max: Available balance'}
                </p>
              </div>

              {/* Action Buttons */}
              <div className="flex space-x-3">
                <motion.button
                  type="button"
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={onClose}
                  className="flex-1 bg-gray-600 hover:bg-gray-500 text-white py-3 px-4 rounded-lg font-medium transition-colors"
                  disabled={loading}
                >
                  Cancel
                </motion.button>
                <motion.button
                  type="submit"
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  className={`flex-1 py-3 px-4 rounded-lg font-medium transition-colors ${
                    action === 'deposit'
                      ? 'bg-green-600 hover:bg-green-700'
                      : 'bg-orange-600 hover:bg-orange-700'
                  } text-white`}
                  disabled={loading || !amount}
                >
                  {loading ? (
                    <div className="flex items-center justify-center space-x-2">
                      <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      <span>Processing...</span>
                    </div>
                  ) : (
                    `${action === 'deposit' ? 'Deposit' : 'Withdraw'} $${amount || '0'}`
                  )}
                </motion.button>
              </div>
            </form>
          )}
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
}

export default WalletModal;
