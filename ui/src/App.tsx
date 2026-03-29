import { useEffect, useCallback } from 'react'
import { useAppDispatch, useAppSelector } from './store/hooks'
import { startGameThunk, submitGuessThunk, addLetter, removeLetter, clearError } from './store/gameSlice'
import { Header } from './components/Header/Header'
import { GameBoard } from './components/GameBoard/GameBoard'
import { Keyboard } from './components/Keyboard/Keyboard'
import { GameOver } from './components/GameOver/GameOver'
import { Toast } from './components/Toast/Toast'
import styles from './App.module.css'

function App() {
  const dispatch = useAppDispatch()
  const { currentInput, status, error } = useAppSelector((s) => s.game)

  useEffect(() => {
    dispatch(startGameThunk())
  }, [dispatch])

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
    </div>
  )
}

export default App

