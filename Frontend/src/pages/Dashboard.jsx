import { useState } from 'react'
import { motion } from 'framer-motion'
import Header from '../components/Header'
import Sidebar from '../components/Sidebar'
import SportsSection from '../components/SportsSection'
import CasinoSection from '../components/CasinoSection'
import UserBets from '../components/UserBets'
import WalletModal from '../components/WalletModal'

function Dashboard() {
  const [activeSection, setActiveSection] = useState('sports')
  const [showWalletModal, setShowWalletModal] = useState(false)
  const [walletAction, setWalletAction] = useState('deposit')
  const [betRefresh, setBetRefresh] = useState(0)

  const handleWalletAction = (action) => {
    setWalletAction(action)
    setShowWalletModal(true)
  }

  const handleBetPlaced = () => {
    setBetRefresh((r) => r + 1)
  }

  return (
    <div className="min-h-screen bg-dark-bg">
      <Header onWalletAction={handleWalletAction} />
      
      <div className="flex">
        <Sidebar 
          activeSection={activeSection} 
          setActiveSection={setActiveSection} 
        />
        
        <main className="flex-1 lg:ml-64">
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
            className="p-4 lg:p-6"
          >
            <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
              <div className="xl:col-span-2">
                {activeSection === 'sports' && <SportsSection onBetPlaced={handleBetPlaced} />}
                {activeSection === 'casino' && <CasinoSection />}
              </div>
              
              <div className="xl:col-span-1">
                <UserBets refreshTrigger={betRefresh} />
              </div>
            </div>
          </motion.div>
        </main>
      </div>

      {showWalletModal && (
        <WalletModal
          action={walletAction}
          onClose={() => setShowWalletModal(false)}
        />
      )}
    </div>
  )
}

export default Dashboard