import styles from './Header.module.css';

export function Header() {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>HEXLE</h1>
      <p className={styles.subtitle}>6-letter word game</p>
    </header>
  );
}
