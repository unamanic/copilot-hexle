import { useState, useEffect } from 'react'
import styles from './CookieBanner.module.css'

const CONSENT_KEY = 'hexle_cookie_consent'

export function CookieBanner() {
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const consent = localStorage.getItem(CONSENT_KEY)
    if (!consent) setVisible(true)
  }, [])

  const handleAccept = () => {
    localStorage.setItem(CONSENT_KEY, 'accepted')
    setVisible(false)
  }

  const handleDecline = () => {
    localStorage.setItem(CONSENT_KEY, 'declined')
    setVisible(false)
  }

  if (!visible) return null

  return (
    <div className={styles.banner} role="dialog" aria-label="Cookie consent">
      <div className={styles.content}>
        <p className={styles.text}>
          🍪 We use a functional cookie (<code>hexle_game_id</code>) to save your current game session for 30 minutes.
          No tracking, no third-party cookies.
        </p>
        <div className={styles.actions}>
          <button className={styles.accept} onClick={handleAccept}>Accept</button>
          <button className={styles.decline} onClick={handleDecline}>Decline</button>
        </div>
      </div>
    </div>
  )
}
