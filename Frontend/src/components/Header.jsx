import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuth } from '../context/AuthContext'
import { ChevronDown, User, LogOut, Wallet, Plus, Minus } from 'lucide-react'

function Header({ onWalletAction }) {
  const [showDropdown, setShowDropdown] = useState(false)
  const { user, balance, balanceLoading, balanceError, logout } = useAuth()

  return (
    <header className="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
      <div className="px-4 lg:px-6 py-4">
        <div className="flex items-center justify-between">
          <motion.h1
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="text-2xl font-bold text-bright-red"
          >
            LuckyLoot
          </motion.h1>

          <div className="flex items-center space-x-4">
            {/* Wallet Section */}
            <div className="flex items-center space-x-2">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => onWalletAction('deposit')}
                className="flex items-center space-x-1 bg-green-600 hover:bg-green-700 px-3 py-2 rounded-lg text-sm font-medium transition-colors"
              >
                <Plus className="w-4 h-4" />
                <span className="hidden sm:inline">Deposit</span>
              </motion.button>

              <div className="bg-gray-800 px-4 py-2 rounded-lg">
                <div className="flex items-center space-x-2">
                  <Wallet className="w-4 h-4 text-green-400" />
                  {balanceLoading ? (
                    <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin inline-block"></span>
                  ) : balanceError ? (
                    <span className="text-red-400 text-sm">Couldn't load</span>
                  ) : (
                    <span className="font-semibold text-green-400">
                      ${typeof balance === 'number' && !isNaN(balance) ? balance.toFixed(2) : '0.00'}
                    </span>
                  )}
                </div>
              </div>

              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => onWalletAction('withdraw')}
                className="flex items-center space-x-1 bg-orange-600 hover:bg-orange-700 px-3 py-2 rounded-lg text-sm font-medium transition-colors"
              >
                <Minus className="w-4 h-4" />
                <span className="hidden sm:inline">Withdraw</span>
              </motion.button>
            </div>

            {/* Profile Dropdown */}
            <div className="relative">
              <motion.button
                whileHover={{ scale: 1.05 }}
                onClick={() => setShowDropdown(!showDropdown)}
                className="flex items-center space-x-2 bg-gray-800 hover:bg-gray-700 px-4 py-2 rounded-lg transition-colors"
              >
                <User className="w-5 h-5" />
                <span className="hidden sm:inline font-medium">
                  {user?.username}
                </span>
                <ChevronDown className="w-4 h-4" />
              </motion.button>

              <AnimatePresence>
                {showDropdown && (
                  <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className="absolute right-0 mt-2 w-48 bg-gray-800 border border-gray-700 rounded-lg shadow-lg"
                  >
                    <div className="p-3 border-b border-gray-700">
                      <p className="text-sm text-gray-300">Signed in as</p>
                      <p className="font-medium truncate">{user?.email}</p>
                    </div>
                    <motion.button
                      whileHover={{ backgroundColor: '#374151' }}
                      onClick={logout}
                      className="w-full flex items-center space-x-2 px-4 py-3 text-left hover:bg-gray-700 transition-colors"
                    >
                      <LogOut className="w-4 h-4" />
                      <span>Sign Out</span>
                    </motion.button>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </div>
      </div>
    </header>
  )
}

export default Header