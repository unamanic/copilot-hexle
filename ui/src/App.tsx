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
import styles from './App.module.css'

const GAME_COOKIE = 'hexle_game_id'

function App() {
  const dispatch = useAppDispatch()
  const { currentInput, status, gameId, error } = useAppSelector((s) => s.game)

  useEffect(() => {
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
      <Header />
      <main className={styles.main}>
        <GameBoard />
        <Keyboard />
      </main>
      <GameOver />
      <CookieBanner />
    </div>
  )
}

export default App

