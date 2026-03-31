import { useState, useEffect } from 'react'
import styles from './InstallPrompt.module.css'

const DISMISSED_KEY = 'hexle_install_dismissed'

interface BeforeInstallPromptEvent extends Event {
  prompt(): Promise<void>
  userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }>
}

function isIos(): boolean {
  return /iphone|ipad|ipod/i.test(navigator.userAgent)
}

function isInStandaloneMode(): boolean {
  return window.matchMedia('(display-mode: standalone)').matches ||
    ('standalone' in navigator && (navigator as Navigator & { standalone?: boolean }).standalone === true)
}

export function InstallPrompt() {
  const [deferredPrompt, setDeferredPrompt] = useState<BeforeInstallPromptEvent | null>(null)
  const [showIosHint, setShowIosHint] = useState(false)
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    if (localStorage.getItem(DISMISSED_KEY)) return
    if (isInStandaloneMode()) return

    if (isIos()) {
      setShowIosHint(true)
      setVisible(true)
      return
    }

    const handler = (e: Event) => {
      e.preventDefault()
      setDeferredPrompt(e as BeforeInstallPromptEvent)
      setVisible(true)
    }
    window.addEventListener('beforeinstallprompt', handler)
    return () => window.removeEventListener('beforeinstallprompt', handler)
  }, [])

  function dismiss() {
    localStorage.setItem(DISMISSED_KEY, '1')
    setVisible(false)
  }

  async function install() {
    if (!deferredPrompt) return
    await deferredPrompt.prompt()
    const { outcome } = await deferredPrompt.userChoice
    if (outcome === 'accepted') {
      setVisible(false)
    }
    setDeferredPrompt(null)
  }

  if (!visible) return null

  return (
    <div className={styles.banner} role="banner">
      <div className={styles.content}>
        {showIosHint ? (
          <p className={styles.text}>
            Install Hexle: tap <strong>Share</strong> then <strong>"Add to Home Screen"</strong> for the best experience.
          </p>
        ) : (
          <p className={styles.text}>
            Add Hexle to your home screen for quick access and a full-screen experience.
          </p>
        )}
        <div className={styles.actions}>
          {!showIosHint && (
            <button className={styles.install} onClick={install}>
              Add to Home Screen
            </button>
          )}
          <button className={styles.dismiss} onClick={dismiss} aria-label="Dismiss install prompt">
            ✕
          </button>
        </div>
      </div>
    </div>
  )
}
