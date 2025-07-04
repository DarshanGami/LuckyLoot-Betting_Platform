import { motion } from 'framer-motion'
import { Trophy, Dice6, Target } from 'lucide-react'

function Sidebar({ activeSection, setActiveSection }) {
  const menuItems = [
    { id: 'sports', label: 'Sports', icon: Trophy },
    { id: 'casino', label: 'Casino', icon: Dice6 }
  ]

  return (
    <aside className="fixed lg:static bottom-0 left-0 right-0 lg:top-auto lg:h-auto bg-gray-900 border-t lg:border-t-0 lg:border-r border-gray-800 lg:w-64 z-40">
      <nav className="p-4">
        <div className="flex lg:flex-col space-x-1 lg:space-x-0 lg:space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon
            const isActive = activeSection === item.id
            
            return (
              <motion.button
                key={item.id}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => setActiveSection(item.id)}
                className={`flex-1 lg:flex-none flex items-center justify-center lg:justify-start space-x-3 px-4 py-3 rounded-lg font-medium transition-colors ${
                  isActive
                    ? 'bg-bright-red text-white'
                    : 'text-gray-300 hover:bg-gray-800 hover:text-white'
                }`}
              >
                <Icon className="w-5 h-5" />
                <span className="hidden lg:inline">{item.label}</span>
              </motion.button>
            )
          })}
        </div>
        
        <div className="hidden lg:block mt-8 p-4 bg-gray-800 rounded-lg">
          <div className="flex items-center space-x-2 mb-2">
            <Target className="w-4 h-4 text-bright-red" />
            <span className="text-sm font-medium">Quick Bet</span>
          </div>
          <p className="text-xs text-gray-400 mb-3">
            Bet on the most popular events
          </p>
          <motion.button
            whileHover={{ scale: 1.05 }}
            className="w-full bg-bright-red hover:bg-red-600 text-white py-2 px-3 rounded text-sm font-medium transition-colors"
          >
            View Hot Picks
          </motion.button>
        </div>
      </nav>
    </aside>
  )
}

export default Sidebar