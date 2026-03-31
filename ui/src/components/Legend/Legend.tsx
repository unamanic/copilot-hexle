import styles from './Legend.module.css';

export function Legend() {
  return (
    <div className={styles.legend}>
      <div className={styles.item}>
        <span className={`${styles.tile} ${styles.correct}`}>A</span>
        <span className={styles.label}>Correct letter, correct position</span>
      </div>
      <div className={styles.item}>
        <span className={`${styles.tile} ${styles.present}`}>B</span>
        <span className={styles.label}>Correct letter, wrong position</span>
      </div>
      <div className={styles.item}>
        <span className={`${styles.tile} ${styles.absent}`}>C</span>
        <span className={styles.label}>Letter not in word</span>
      </div>
    </div>
  );
}
