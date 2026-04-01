import { useEffect, useCallback } from 'react'
import Cookies from 'js-cookie'
import { useAppDispatch, useAppSelector } from './store/hooks'
import { startGameThunk, submitGuessThunk, addLetter, removeLetter, clearError, resumeGameThunk } from './store/gameSlice'
import { Header } from './components/Header/Header'
import { GameBoard } from './components/GameBoard/GameBoard'
import { Keyboard } from './components/Keyboard/Keyboard'
import { GameOver } from './components/GameOver/GameOver'
import { Toast } from './components/Toast/Toast'
import { CookieBanner } from './components/CookieBanner/CookieBanner'
import { InstallPrompt } from './components/InstallPrompt/InstallPrompt'
import { Legend } from './components/Legend/Legend'
import { useTheme } from './hooks/useTheme'
import styles from './App.module.css'

const GAME_COOKIE = 'hexle_game_id'

function App() {
  const dispatch = useAppDispatch()
  const { currentInput, status, gameId, error } = useAppSelector((s) => s.game)
  const { theme, toggleTheme } = useTheme()

  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const challengeToken = params.get('challenge')

    if (challengeToken) {
      // Remove the token from the URL immediately so it isn't re-used on refresh
      window.history.replaceState({}, '', window.location.pathname)
      dispatch(startGameThunk(challengeToken))
      return
    }

    const existingId = Cookies.get(GAME_COOKIE)
    if (existingId) {
      dispatch(resumeGameThunk(existingId)).then((action) => {
        if (resumeGameThunk.rejected.match(action)) {
          Cookies.remove(GAME_COOKIE)
          dispatch(startGameThunk())
        }
      })
    } else {
      dispatch(startGameThunk())
    }
  }, [dispatch])

  useEffect(() => {
    if (gameId) {
      Cookies.set(GAME_COOKIE, gameId, {
        expires: 1 / 48,     // 30 minutes
        secure: true,        // HTTPS only
        sameSite: 'Strict',  // CSRF protection
      })
    }
  }, [gameId])

  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (status && status !== 'IN_PROGRESS') return
      if (e.ctrlKey || e.altKey || e.metaKey) return
      if (e.key === 'Backspace') {
        dispatch(removeLetter())
      } else if (e.key === 'Enter') {
        if (currentInput.length === 6) {
          dispatch(submitGuessThunk(currentInput))
        }
      } else if (/^[a-zA-Z]$/.test(e.key)) {
        dispatch(addLetter(e.key))
      }
    },
    [dispatch, currentInput, status],
  )

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [handleKeyDown])

  return (
    <div className={styles.app}>
      <Toast message={error} onDismiss={() => dispatch(clearError())} />
      <Header theme={theme} onToggleTheme={toggleTheme} />
      <main className={styles.main}>
        <GameBoard />
        <Legend />
        <Keyboard />
      </main>
      <GameOver />
      <CookieBanner />
      <InstallPrompt />
    </div>
  )
}

export default App

