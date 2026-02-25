import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Lab1Task {

    private static final String FILE_NAME = "input_data.txt";
    private static final String OUT_FILE_NAME = "results.txt";

    private static int N;
    private static int maxIterations;
    private static final double tolerance = 1e-15;

    public static void main(String[] args) {
        generateInputData(15_000_000, 200);

        readInputData();

        System.out.println("Розмір сітки (N): " + N);
        System.out.println("Макс. ітерацій: " + maxIterations);

        double[] resultSeq = solveSequential();
        saveResults("Послідовний розв'язок", resultSeq, false);

        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Кількість потоків: " + numThreads);

        double[] resultPar = solveParallel(numThreads);
        saveResults("Паралельний розв'язок", resultPar, true);
    }

    private static void generateInputData(int gridSize, int maxIter) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println(gridSize);
            writer.println(maxIter);
            System.out.println("Вхідні дані згенеровано у файл " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Помилка запису файлу: " + e.getMessage());
        }
    }

    private static void readInputData() {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            N = scanner.nextInt();
            maxIterations = scanner.nextInt();
        } catch (FileNotFoundException e) {
            System.err.println("Файл не знайдено: " + e.getMessage());
        }
    }

    private static void saveResults(String title, double[] y, boolean append) {
        DecimalFormat df = new DecimalFormat("0.0000E0");

        try (PrintWriter writer = new PrintWriter(new FileWriter(OUT_FILE_NAME, append))) {
            writer.println("=== " + title + " ===");
            writer.println("x\ty");
            double h = 1.0 / N;
            for (int i = 0; i <= N; i += Math.max(1, N / 20)) {
                String xFormatted = df.format(i * h);
                String yFormatted = df.format(y[i]);
                writer.println(xFormatted + "\t" + yFormatted);
            }
            writer.println();
            System.out.println("Результати (" + title + ") збережено у файл " + OUT_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Помилка запису результатів: " + e.getMessage());
        }
    }

    private static double[] solveSequential() {
        double[] y = new double[N + 1];
        double[] yNew = new double[N + 1];
        double h = 1.0 / N;
        double h2 = h * h;
        double denominator = 2 + h2;

        double[] consts = new double[N + 1];
        for (int i = 1; i < N; i++) {
            consts[i] = h2 * (i * h);
        }

        long startTime = System.nanoTime();

        int iter;
        for (iter = 0; iter < maxIterations; iter++) {

            double[] currentY = (iter % 2 == 0) ? y : yNew;
            double[] nextY = (iter % 2 == 0) ? yNew : y;

            double maxDiff = 0.0;
            for (int i = 1; i < N; i++) {
                nextY[i] = (currentY[i - 1] + currentY[i + 1] + consts[i]) / denominator;
                maxDiff = Math.max(maxDiff, Math.abs(nextY[i] - currentY[i]));
            }

            if (maxDiff < tolerance) break;
        }

        if (iter % 2 != 0) {
            System.arraycopy(yNew, 1, y, 1, N - 1);
        }

        long endTime = System.nanoTime();
        System.out.println("Послідовний час: " + (endTime - startTime) / 1_000_000 + " мс");

        return y;
    }

    private static volatile boolean converged;

    private static double[] solveParallel(int numThreads) {
        converged = false;
        double[] y = new double[N + 1];
        double[] yNew = new double[N + 1];
        double h = 1.0 / N;
        double h2 = h * h;
        double denominator = 2 + h2;

        Thread[] threads = new Thread[numThreads];
        double[] threadMaxDiffs = new double[numThreads];

        CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
            double currentGlobalMax = 0.0;
            for (double diff : threadMaxDiffs) {
                currentGlobalMax = Math.max(currentGlobalMax, diff);
            }

            if (currentGlobalMax < tolerance) {
                converged = true;
            }
        });

        int chunkSize = (N - 1) / numThreads;

        long startTime = System.nanoTime();

        for (int threadId = 0; threadId < numThreads; threadId++) {
            int tId = threadId;
            int startIdx = 1 + tId * chunkSize;
            int endIdx = (tId == numThreads - 1) ? N : startIdx + chunkSize;

            threads[tId] = new Thread(() -> {
                double[] localConsts = new double[endIdx - startIdx];
                for (int i = startIdx; i < endIdx; i++) {
                    localConsts[i - startIdx] = h2 * (i * h);
                }

                int iter;
                for (iter = 0; iter < maxIterations; iter++) {
                    if (converged) break;

                    double[] currentY = (iter % 2 == 0) ? y : yNew;
                    double[] nextY = (iter % 2 == 0) ? yNew : y;

                    double localMaxDiff = 0.0;
                    for (int i = startIdx; i < endIdx; i++) {
                        nextY[i] = (currentY[i - 1] + currentY[i + 1] + localConsts[i - startIdx]) / denominator;
                        localMaxDiff = Math.max(localMaxDiff, Math.abs(nextY[i] - currentY[i]));
                    }

                    threadMaxDiffs[tId] = localMaxDiff;

                    try {
                        barrier.await();
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (iter % 2 != 0) {
                    System.arraycopy(yNew, startIdx, y, startIdx, endIdx - startIdx);
                }
            });
            threads[tId].start();
        }

        for (Thread thread : threads) {
            try { thread.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        long endTime = System.nanoTime();
        System.out.println("Паралельний час: " + (endTime - startTime) / 1_000_000 + " мс");
        return y;
    }
}