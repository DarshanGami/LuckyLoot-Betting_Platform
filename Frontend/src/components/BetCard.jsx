import { useState } from 'react'
import { motion } from 'framer-motion'
import { useAuth } from '../context/AuthContext'
import { Clock, Trophy, TrendingUp } from 'lucide-react'

function BetCard({ match, type }) {
  const [selectedBet, setSelectedBet] = useState(null)
  const [showBetSlip, setShowBetSlip] = useState(false)
  const [betAmount, setBetAmount] = useState(10)
  const { placeBet } = useAuth()

  const handleBetClick = (betType, odds) => {
    setSelectedBet({ type: betType, odds })
    setShowBetSlip(true)
  }

  const handlePlaceBet = () => {
    if (selectedBet && betAmount > 0) {
      const bet = {
        type: type,
        match: type === 'sports' ? `${match.team1} vs ${match.team2}` : match.name,
        bet: selectedBet.type,
        odds: selectedBet.odds,
        stake: betAmount
      }
      placeBet(bet)
      setShowBetSlip(false)
      setSelectedBet(null)
      setBetAmount(10)
    }
  }

  const potentialWin = selectedBet ? (betAmount * selectedBet.odds).toFixed(2) : 0

  return (
    <div className="bg-gray-800 border border-gray-700 rounded-lg p-6 hover:border-bright-red transition-colors">
      {/* Match Header */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-2">
          <div className="bg-bright-red p-1 rounded">
            <Trophy className="w-4 h-4 text-white" />
          </div>
          <span className="text-sm font-medium text-bright-red uppercase">
            {match.sport || 'Game'}
          </span>
        </div>
        
        {match.isLive && (
          <div className="flex items-center space-x-1 text-green-400">
            <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
            <span className="text-sm font-medium">LIVE</span>
          </div>
        )}
      </div>

      {/* Teams/Players */}
      <div className="mb-4">
        <div className="flex items-center justify-between mb-2">
          <h3 className="text-lg font-semibold text-white">{match.team1}</h3>
          {match.score && (
            <span className="text-2xl font-bold text-white">
              {match.score.split('-')[0]}
            </span>
          )}
        </div>
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-white">{match.team2}</h3>
          {match.score && (
            <span className="text-2xl font-bold text-white">
              {match.score.split('-')[1]}
            </span>
          )}
        </div>
      </div>

      {/* Match Time */}
      <div className="flex items-center justify-center mb-4">
        <div className="flex items-center space-x-1 text-gray-400">
          <Clock className="w-4 h-4" />
          <span className="text-sm">{match.time}</span>
        </div>
      </div>

      {/* Betting Odds */}
      <div className="grid grid-cols-2 md:grid-cols-3 gap-2 mb-4">
        {match.odds.team1 && (
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => handleBetClick(`${match.team1} Win`, match.odds.team1)}
            className="bg-gray-700 hover:bg-bright-red text-center py-3 px-2 rounded-lg transition-colors"
          >
            <div className="text-xs text-gray-400 mb-1">1</div>
            <div className="text-sm font-semibold text-white">{match.odds.team1}</div>
          </motion.button>
        )}
        
        {match.odds.draw && (
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => handleBetClick('Draw', match.odds.draw)}
            className="bg-gray-700 hover:bg-bright-red text-center py-3 px-2 rounded-lg transition-colors"
          >
            <div className="text-xs text-gray-400 mb-1">X</div>
            <div className="text-sm font-semibold text-white">{match.odds.draw}</div>
          </motion.button>
        )}
        
        {match.odds.team2 && (
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => handleBetClick(`${match.team2} Win`, match.odds.team2)}
            className="bg-gray-700 hover:bg-bright-red text-center py-3 px-2 rounded-lg transition-colors"
          >
            <div className="text-xs text-gray-400 mb-1">2</div>
            <div className="text-sm font-semibold text-white">{match.odds.team2}</div>
          </motion.button>
        )}
      </div>

      {/* Bet Slip Modal */}
      {showBetSlip && (
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
        >
          <div className="bg-gray-800 rounded-lg p-6 max-w-md w-full">
            <h3 className="text-lg font-semibold text-white mb-4">Place Bet</h3>
            
            <div className="bg-gray-700 p-4 rounded-lg mb-4">
              <p className="text-sm text-gray-400">Match</p>
              <p className="text-white font-medium">{match.team1} vs {match.team2}</p>
              <p className="text-sm text-gray-400 mt-1">Selection</p>
              <p className="text-white font-medium">{selectedBet?.type}</p>
              <p className="text-sm text-gray-400 mt-1">Odds</p>
              <p className="text-bright-red font-bold">{selectedBet?.odds}</p>
            </div>

            <div className="mb-4">
              <label className="block text-sm text-gray-400 mb-2">Bet Amount ($)</label>
              <input
                type="number"
                value={betAmount}
                onChange={(e) => setBetAmount(Number(e.target.value))}
                className="w-full bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                min="1"
              />
            </div>

            <div className="bg-gray-700 p-3 rounded-lg mb-4">
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Potential Win:</span>
                <span className="text-green-400 font-bold">${potentialWin}</span>
              </div>
            </div>

            <div className="flex space-x-3">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => setShowBetSlip(false)}
                className="flex-1 bg-gray-600 hover:bg-gray-500 text-white py-2 px-4 rounded-lg transition-colors"
              >
                Cancel
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handlePlaceBet}
                className="flex-1 bg-bright-red hover:bg-red-600 text-white py-2 px-4 rounded-lg transition-colors"
              >
                Place Bet
              </motion.button>
            </div>
          </div>
        </motion.div>
      )}
    </div>
  )
}

export default BetCard