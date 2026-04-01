import styles from './Header.module.css';

interface HeaderProps {
  theme: 'dark' | 'light';
  onToggleTheme: () => void;
  onShowAbout: () => void;
}

export function Header({ theme, onToggleTheme, onShowAbout }: HeaderProps) {
  return (
    <header className={styles.header}>
      <div className={styles.titleRow}>
        <button
          className={styles.iconButton}
          onClick={onShowAbout}
          aria-label="About Hexle"
          title="About"
        >
          ℹ️
        </button>
        <h1 className={styles.title}>HEXLE</h1>
        <button
          className={styles.iconButton}
          onClick={onToggleTheme}
          aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
          title={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
        >
          {theme === 'dark' ? '☀️' : '🌙'}
        </button>
      </div>
      <p className={styles.subtitle}>6-letter word game</p>
    </header>
  );
}
