import { motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import { getUserBets, cashoutBet } from '../api/betApi'
import { Trophy } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

function UserBets({ refreshTrigger }) {
  const [userBets, setUserBets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [cashoutLoading, setCashoutLoading] = useState(null)
  const [cashoutError, setCashoutError] = useState(null)
  const [cashoutSuccess, setCashoutSuccess] = useState(null)
  const { setBalance, refreshBalance } = useAuth();

  useEffect(() => {
    async function fetchBets() {
      setLoading(true)
      setError(null)
      try {
        const data = await getUserBets()
        setUserBets(data)
      } catch (err) {
        setError('Failed to load bets.')
      } finally {
        setLoading(false)
      }
    }
    fetchBets()
  }, [refreshTrigger])

  const handleCashout = async (betId) => {
    setCashoutLoading(betId)
    setCashoutError(null)
    setCashoutSuccess(null)
    try {
      const result = await cashoutBet(betId)
      setUserBets((prev) => prev.map(bet => bet.id === betId ? { ...bet, cashout: true, cashoutAmount: result.cashoutAmount, settled: true } : bet))
      if (result.cashoutAmount) {
        setBalance(prev => prev + Number(result.cashoutAmount))
        if (refreshBalance) refreshBalance()
      }
      setCashoutSuccess('Cashout Successful!')
      setTimeout(() => setCashoutSuccess(null), 2000)
    } catch (err) {
      setCashoutError('Cashout failed')
    } finally {
      setCashoutLoading(null)
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="bg-gray-800 rounded-lg p-6"
    >
      <div className="flex items-center space-x-2 mb-6">
        <Trophy className="w-6 h-6 text-bright-red" />
        <h2 className="text-xl font-bold text-white">My Bets</h2>
      </div>
      {loading ? (
        <div className="flex justify-center items-center h-32">
          <div className="loader border-4 border-t-4 border-gray-200 rounded-full w-10 h-10 animate-spin border-bright-red"></div>
        </div>
      ) : error ? (
        <div className="text-red-500 text-center py-4">{error}</div>
      ) : userBets.length === 0 ? (
        <div className="text-center py-8">
          <Trophy className="w-12 h-12 text-gray-600 mx-auto mb-3" />
          <p className="text-gray-400">No betting history yet</p>
          <p className="text-sm text-gray-500">Your completed bets will appear here</p>
        </div>
      ) : (
        <div className="space-y-4">
          {userBets.map((bet) => (
            <motion.div
              key={bet.id || bet.betId}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="bg-white rounded-2xl shadow-lg p-5 border border-gray-200 flex flex-col gap-2"
            >
              {/* Match Name */}
              <div className="flex items-center justify-between mb-1">
                <span className="text-xs text-gray-400">Bet ID: <span className="text-gray-600 font-mono">{bet.betId || '-'}</span></span>
                <span className={`text-xs font-bold ${bet.settled ? (bet.won ? 'text-green-600' : 'text-red-500') : 'text-yellow-600'}`}>
                  {bet.settled ? (bet.won ? 'WON' : 'LOST') : bet.cashout ? 'CASHED OUT' : 'ACTIVE'}
                </span>
              </div>
              <div className="text-lg font-bold text-gray-800 mb-1">{bet.matchId ? bet.matchId.replace(/_/g, ' vs ') : '-'}</div>
              <div className="flex flex-wrap gap-4 mb-2">
                <div>
                  <div className="text-xs text-gray-400">Team</div>
                  <div className="font-semibold text-bright-red text-base">{bet.team || '-'}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-400">Odds</div>
                  <div className="font-semibold text-yellow-500 text-base">{bet.oddAtBet ? bet.oddAtBet : '-'}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-400">Amount</div>
                  <div className="font-semibold text-blue-600 text-base">${bet.amount ? Number(bet.amount).toFixed(2) : '0.00'}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-400">Potential Win</div>
                  <div className="font-semibold text-green-600 text-base">${(bet.amount && bet.oddAtBet) ? (Number(bet.amount) * Number(bet.oddAtBet)).toFixed(2) : '0.00'}</div>
                </div>
                {bet.cashout && (
                  <div>
                    <div className="text-xs text-gray-400">Cashout Amount</div>
                    <div className="font-semibold text-orange-600 text-base">${bet.cashoutAmount ? Number(bet.cashoutAmount).toFixed(2) : '0.00'}</div>
                  </div>
                )}
                {bet.settled && bet.won && (
                  <div>
                    <div className="text-xs text-gray-400">Win Amount</div>
                    <div className="font-semibold text-green-600 text-base">${bet.winAmount ? Number(bet.winAmount).toFixed(2) : '0.00'}</div>
                  </div>
                )}
              </div>
              <div className="flex items-center justify-between mt-2">
                <div className="text-xs text-gray-400">Placed: <span className="text-gray-600">{bet.placedAt ? new Date(bet.placedAt).toLocaleString() : '-'}</span></div>
                {(!bet.settled && !bet.cashout) && (
                  <button
                    className={`px-4 py-2 rounded-lg font-semibold text-white bg-yellow-500 hover:bg-yellow-600 transition-colors focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 ${cashoutLoading === (bet.id || bet.betId) ? 'opacity-60 cursor-wait' : 'cursor-pointer'} disabled:opacity-50 disabled:cursor-not-allowed`}
                    onClick={() => handleCashout(bet.id || bet.betId)}
                    disabled={cashoutLoading === (bet.id || bet.betId)}
                  >
                    {cashoutLoading === (bet.id || bet.betId) ? (
                      <span className="flex items-center gap-2"><span className="loader w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span> Cashing out...</span>
                    ) : 'Cashout'}
                  </button>
                )}
              </div>
              {cashoutError && cashoutLoading === (bet.id || bet.betId) && (
                <div className="text-red-500 text-xs mt-2">{cashoutError}</div>
              )}
              {cashoutSuccess && cashoutLoading === null && (
                <div className="text-green-600 text-xs mt-2">{cashoutSuccess}</div>
              )}
            </motion.div>
          ))}
        </div>
      )}
    </motion.div>
  )
}

export default UserBets