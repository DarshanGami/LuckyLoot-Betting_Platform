import { motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import { getAllMatches } from '../api/matchApi'
import { placeBet } from '../api/betApi'

function SportsSection({ onBetPlaced }) {
  const [matches, setMatches] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [betModal, setBetModal] = useState({ open: false, match: null, team: null, odd: null })
  const [betAmount, setBetAmount] = useState('')
  const [betLoading, setBetLoading] = useState(false)
  const [betError, setBetError] = useState(null)
  const [betSuccess, setBetSuccess] = useState(false)

  useEffect(() => {
    async function fetchMatches() {
      setLoading(true)
      setError(null)
      try {
        const data = await getAllMatches()
        setMatches(data)
      } catch (err) {
        setError('Failed to load matches.')
      } finally {
        setLoading(false)
      }
    }
    fetchMatches()
  }, [])

  const openBetModal = (match, team, odd) => {
    setBetModal({ open: true, match, team, odd })
    setBetAmount('')
    setBetError(null)
    setBetSuccess(false)
  }

  const closeBetModal = () => {
    setBetModal({ open: false, match: null, team: null, odd: null })
    setBetAmount('')
    setBetError(null)
    setBetSuccess(false)
  }

  const handleBetSubmit = async (e) => {
    e.preventDefault()
    setBetLoading(true)
    setBetError(null)
    setBetSuccess(false)
    try {
      await placeBet({
        matchId: betModal.match._id,
        team: betModal.team,
        oddAtBet: betModal.odd,
        amount: parseFloat(betAmount)
      })
      setBetSuccess(true)
      if (onBetPlaced) onBetPlaced()
      setTimeout(() => {
        closeBetModal()
      }, 1000)
    } catch (err) {
      setBetError('Failed to place bet.')
    } finally {
      setBetLoading(false)
    }
  }

  // Helper for odds buttons (for unique keys)
  const getOddsArray = (match) => {
    // Football: 1 X 2, else just 2 outcomes
    if (match.odd1 && match.odd2 && match.oddDraw) {
      return [
        { label: '1', value: match.odd1, team: match.team1 },
        { label: 'X', value: match.oddDraw, team: 'Draw' },
        { label: '2', value: match.odd2, team: match.team2 },
      ]
    }
    return [
      { label: '1', value: match.odd1, team: match.team1 },
      { label: '2', value: match.odd2, team: match.team2 },
    ]
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-white mb-2">Sports Betting</h2>
        <p className="text-gray-400">Live matches and upcoming events</p>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-40">
          <div className="loader border-4 border-t-4 border-gray-200 rounded-full w-12 h-12 animate-spin border-bright-red"></div>
        </div>
      ) : error ? (
        <div className="text-red-500 text-center py-4">{error}</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
          {matches.map((match, index) => (
            <motion.div
              key={match._id || index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="rounded-xl shadow bg-[#181f2a] border border-[#232b3b] p-0 overflow-hidden relative"
            >
              {/* Header */}
              <div className="flex items-center justify-between px-4 pt-4">
                <div className="flex items-center gap-2">
                  <span className="bg-[#b91c1c] text-xs text-white font-bold px-2 py-1 rounded flex items-center">
                    <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" /></svg>
                    FOOTBALL
                  </span>
                  {match.live && (
                    <span className="ml-2 text-green-400 text-xs font-bold flex items-center">
                      <span className="w-2 h-2 bg-green-400 rounded-full mr-1"></span>LIVE
                    </span>
                  )}
                </div>
                <span className="text-xs text-gray-400 flex items-center"><svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" /></svg>{match.time}</span>
              </div>
              {/* Teams & Score */}
              <div className="px-4 pt-2 pb-1">
                <div className="flex justify-between items-center">
                  <div>
                    <div className="text-white font-bold text-lg">{match.team1}</div>
                    <div className="text-2xl text-white font-bold">{match.score1}</div>
                  </div>
                  <div className="text-center">
                    <span className="text-xs text-gray-400">vs</span>
                  </div>
                  <div className="text-right">
                    <div className="text-white font-bold text-lg">{match.team2}</div>
                    <div className="text-2xl text-white font-bold">{match.score2}</div>
                  </div>
                </div>
                <div className="text-xs text-gray-400 mt-1">{match.extraInfo}</div>
              </div>
              {/* Odds Buttons */}
              <div className="flex justify-between gap-2 px-4 pb-4">
                <button
                  key={match._id + '-1'}
                  className="flex-1 bg-[#232b3b] hover:bg-[#b91c1c] text-white rounded-lg py-2 mt-2 flex flex-col items-center border border-[#232b3b] hover:border-[#b91c1c] transition-colors"
                  onClick={() => openBetModal(match, match.team1, match.odd1)}
                >
                  <span className="text-xs font-bold">1</span>
                  <span className="text-base font-semibold">{match.odd1}</span>
                </button>
                {match.oddDraw && (
                  <button
                    key={match._id + '-X'}
                    className="flex-1 bg-[#232b3b] hover:bg-[#b91c1c] text-white rounded-lg py-2 mt-2 flex flex-col items-center border border-[#232b3b] hover:border-[#b91c1c] transition-colors"
                    onClick={() => openBetModal(match, 'Draw', match.oddDraw)}
                  >
                    <span className="text-xs font-bold">X</span>
                    <span className="text-base font-semibold">{match.oddDraw}</span>
                  </button>
                )}
                <button
                  key={match._id + '-2'}
                  className="flex-1 bg-[#232b3b] hover:bg-[#b91c1c] text-white rounded-lg py-2 mt-2 flex flex-col items-center border border-[#232b3b] hover:border-[#b91c1c] transition-colors"
                  onClick={() => openBetModal(match, match.team2, match.odd2)}
                >
                  <span className="text-xs font-bold">2</span>
                  <span className="text-base font-semibold">{match.odd2}</span>
                </button>
              </div>
              <div className="text-xs text-gray-400 mt-2 px-4 pb-2">Status: <span className="font-semibold text-white">{match.status}</span></div>
            </motion.div>
          ))}
        </div>
      )}

      {/* Bet Modal */}
      {betModal.open && betModal.match && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-[#232b3b] rounded-xl p-6 w-full max-w-md relative shadow-xl">
            <button
              className="absolute top-2 right-2 text-gray-400 hover:text-white text-2xl"
              onClick={closeBetModal}
            >
              &times;
            </button>
            <h3 className="text-lg font-bold text-white mb-4">Place Bet</h3>
            <div className="bg-[#181f2a] rounded-lg p-4 mb-4">
              <div className="mb-2">
                <span className="text-xs text-gray-400">Match</span>
                <div className="font-bold text-white text-base">{betModal.match.team1} vs {betModal.match.team2}</div>
              </div>
              <div className="mb-2">
                <span className="text-xs text-gray-400">Selection</span>
                <div className="font-semibold text-white text-base">{betModal.team}{betModal.team === 'Draw' ? '' : ' Win'}</div>
              </div>
              <div className="mb-2">
                <span className="text-xs text-gray-400">Odds</span>
                <div className="font-semibold text-red-400 text-base">{betModal.odd}</div>
              </div>
            </div>
            <form onSubmit={handleBetSubmit}>
              <label className="block text-xs text-gray-400 mb-1">Bet Amount ($)</label>
              <input
                type="number"
                min="1"
                step="any"
                className="w-full mb-3 px-3 py-2 rounded bg-[#181f2a] text-white border border-[#232b3b] focus:outline-none focus:ring-2 focus:ring-bright-red"
                placeholder="Enter amount"
                value={betAmount}
                onChange={e => setBetAmount(e.target.value)}
                required
              />
              {/* Potential Win */}
              <div className="mb-3 flex items-center justify-between">
                <span className="text-xs text-gray-400">Potential Win:</span>
                <span className="text-green-400 font-bold text-base">
                  {betAmount && betModal.odd ? `$${(parseFloat(betAmount) * parseFloat(betModal.odd)).toFixed(2)}` : '$0.00'}
                </span>
              </div>
              {betError && <div className="text-red-500 text-sm mb-2">{betError}</div>}
              {betSuccess && <div className="text-green-500 text-sm mb-2">Bet placed!</div>}
              <div className="flex gap-2 mt-2">
                <button
                  type="button"
                  className="flex-1 py-2 bg-gray-600 hover:bg-gray-700 text-white font-semibold rounded transition-colors"
                  onClick={closeBetModal}
                  disabled={betLoading}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 bg-bright-red hover:bg-red-600 text-white font-semibold rounded transition-colors"
                  disabled={betLoading}
                >
                  {betLoading ? 'Placing...' : 'Place Bet'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="bg-gray-800 rounded-lg p-6 mt-8">
        <h3 className="text-lg font-semibold text-white mb-4">Popular Bet Types</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {['Match Winner', 'Over/Under Goals', 'Both Teams Score', 'Handicap'].map((betType, idx) => (
            <motion.div
              key={betType + idx}
              whileHover={{ scale: 1.05 }}
              className="bg-gray-700 p-4 rounded-lg text-center cursor-pointer hover:bg-gray-600 transition-colors"
            >
              <p className="text-sm font-medium text-white">{betType}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </motion.div>
  )
}

export default SportsSection