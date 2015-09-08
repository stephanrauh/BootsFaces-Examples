package de.beyondjava.bootsfaces.chess.objectOrientedEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.beyondjava.bootsfaces.chess.common.Move;
import de.beyondjava.bootsfaces.chess.common.Piece;
import de.beyondjava.bootsfaces.chess.common.Settings;
import de.beyondjava.bootsfaces.chess.exceptions.BlackIsCheckMateException;
import de.beyondjava.bootsfaces.chess.exceptions.EndOfGameException;
import de.beyondjava.bootsfaces.chess.exceptions.KingLostException;
import de.beyondjava.bootsfaces.chess.exceptions.StaleMateException;
import de.beyondjava.bootsfaces.chess.exceptions.WhiteIsCheckMateException;

public class Chessboard extends ChessboardBasis {

	public Chessboard() {
        super();
    }

    public Chessboard(boolean activePlayerIsWhite, ChessboardBasis board) {
        super(activePlayerIsWhite, board);
    }

    public Chessboard(boolean activePlayerIsWhite, Piece... pieces) {
        super(activePlayerIsWhite, pieces);
    }


    public Chessboard(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn, int promotedPiece) {
        super(oldBoard, fromRow, fromColumn, toRow, toColumn, promotedPiece);
    }

    public Move findBestMove() throws EndOfGameException {
        Chessboard.evaluatedPositions = 0;
        Chessboard.totalTime = 0;
        Settings settings = new Settings();
        long start = System.nanoTime();
		int[] bestMoves = activePlayerIsWhite ? findBestWhiteMoves(settings.getLookAhead(), settings.getMovesToConsider(), settings.isMultithreading()) 
				: findBestBlackMoves(settings.getLookAhead(), settings.getMovesToConsider(), settings.isMultithreading());
        Chessboard.realTimeOfCalculation = System.nanoTime() - start;
//        System.out.println("Calculation took " + ((Chessboard.realTimeOfCalculation / 1000) / 1000.0d) + "ms Evalutated POSITIONAL_VALUES:" + NumberFormat.getInstance().format( Chessboard.evaluatedPositions));
//        System.out.println("evaluation took  " + ((Chessboard.totalTime/1000)/1000) + " ms");
//        System.out.println("Average evaluation: " + Chessboard.totalTime/evaluatedPositions + " ns") ;
        int move = bestMoves[0];
        int fromRow = (move >> 12) & 0x000F;
        int fromColumn = (move >> 8) & 0x000F;
        int toRow = (move >> 4) & 0x000F;
        int toColumn = move & 0x000F;
        int promotion = (move >> 16) & 0x00FF;
        int capturedValue = (move >> 24);

        return new Move(getChessPiece(fromColumn, fromRow), fromRow, fromColumn, toRow, toColumn, 0, false, false, 0);
    }

    public int[] findBestBlackMoves(int lookAhead, int movesToConsider, boolean multithreading) throws EndOfGameException {
        Comparator moveComparator = new BlackMoveComparator();
        int[] moves = Arrays.copyOf(blackMoves, numberOfBlackMoves);
        try {
            List<XMove> evaluatedMoves = findBestMovesWithoutRecursion(moves, s_BLACK);
            if (evaluatedMoves.size() == 0) {
                if (isBlackKingThreatened)
                {
                    throw new BlackIsCheckMateException();
                }
                if (isWhiteKingThreatened)
                {
                    throw new WhiteIsCheckMateException();
                }
                throw new StaleMateException();
            }
            // eliminate silly moves
            if (lookAhead >= 0) {
                evaluatedMoves = findBestBlackMovesRecursively(0, evaluatedMoves.size(), moveComparator, evaluatedMoves, multithreading);
            }
            List<XMove> bestEvaluatedMoves = new ArrayList<>();

            if (lookAhead > 0) {
                bestEvaluatedMoves = findBestBlackMovesRecursively(lookAhead, movesToConsider - 1, moveComparator, evaluatedMoves, multithreading);
            } else {
                for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
                    XMove e = (XMove) evaluatedMoves.get(i);
                    bestEvaluatedMoves.add(e);
                }
            }
            int size = bestEvaluatedMoves.size();
//            if (size == 0) {
//                System.err.println("No possible move left?");
//            }
            int[] bestMoves = new int[size];
            for (int i = 0; i < movesToConsider && i < size; i++) {
                bestMoves[i] = bestEvaluatedMoves.get(i).move;
            }
            return bestMoves;
        } catch (KingLostException p_checkmate) {
            throw new WhiteIsCheckMateException();
        }
    }

    private List<XMove> findBestBlackMovesRecursively(int lookAhead, int movesToConsider, Comparator moveComparator, List<XMove> evaluatedMoves, boolean multithreading) throws EndOfGameException {
        List<XMove> bestEvaluatedMoves;
        if (multithreading)
            bestEvaluatedMoves = findBestBlackMovesRecursivelyMultiThreaded(lookAhead, movesToConsider, evaluatedMoves);
        else

            bestEvaluatedMoves = findBestBlackMovesRecursivelySingleThreaded(lookAhead, movesToConsider, evaluatedMoves);
        Collections.sort(bestEvaluatedMoves, moveComparator);
        return bestEvaluatedMoves;
    }

    private List<XMove> findBestBlackMovesRecursivelyMultiThreaded(final int lookAhead, final int movesToConsider, final List<XMove> evaluatedMoves) throws EndOfGameException {

        final List<XMove> bestEvaluatedMoves = new ArrayList<>();
        int proc = Runtime.getRuntime().availableProcessors();
        int mtc = 0;
        while (mtc<=movesToConsider)
        {
            mtc += proc;
        }
        ExecutorService executor = Executors.newFixedThreadPool(evaluatedMoves.size());
        try {
            List<Future<Object>> list = new ArrayList<Future<Object>>();
            for (int i = 0; i < evaluatedMoves.size() && i < mtc; i++) {
//                for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
                final XMove e = (XMove) evaluatedMoves.get(i);

                Callable<Object> worker = new Callable<Object>() {
                    public Object call() throws EndOfGameException {
                        try {
                            int opponentsMoveToConsider = movesToConsider;
                            if (lookAhead <= 0) {
                                opponentsMoveToConsider = 1;
                            }
                            int[] whiteMoves = e.boardAfterMove.findBestWhiteMoves(lookAhead - 1, opponentsMoveToConsider, false);
                            convertFirstMoveToXMove(e, whiteMoves);
                            synchronized (bestEvaluatedMoves) {
                                bestEvaluatedMoves.add(e);
                            }
                        } catch (BlackIsCheckMateException p_impossibleMove) {
                            e.checkmate = true;
                            e.whiteTotalValue = 1000000;
                            e.blackTotalValue = -1000000;
                            bestEvaluatedMoves.add(e);
                        } catch (WhiteIsCheckMateException p_winningMove) {
                            e.checkmate = true;
                            e.whiteTotalValue = -1000000;
                            e.blackTotalValue = 1000000;
                            bestEvaluatedMoves.add(e);
                            // break; // no need to look at the other moves
                        }
                        catch (EndOfGameException p_stalemate)
                        {
                            e.stalemate = true;
                            e.whiteTotalValue = 0;
                            e.blackTotalValue = 0;
                            bestEvaluatedMoves.add(e);
                        }
                        return null;
                    }
                };
                Future<Object> submit = executor.submit(worker);
                list.add(submit);

            }
            for (Future<Object> future : list) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            executor.shutdown();
        }
        return bestEvaluatedMoves;
    }

    private List<XMove> findBestBlackMovesRecursivelySingleThreaded(int lookAhead, int movesToConsider, List<XMove> evaluatedMoves) throws EndOfGameException {
        List<XMove> bestEvaluatedMoves = new ArrayList<>();
        for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
            XMove e = (XMove) evaluatedMoves.get(i);
            try {
                int opponentsMoveToConsider = movesToConsider;
                if (lookAhead <= 0) {
                    opponentsMoveToConsider = 1;
                }
                int[] whiteMoves = e.boardAfterMove.findBestWhiteMoves(lookAhead - 1, opponentsMoveToConsider, false);
                convertFirstMoveToXMove(e, whiteMoves);
                bestEvaluatedMoves.add(e);
            } catch (BlackIsCheckMateException p_impossibleMove) {
                e.checkmate = true;
                e.whiteTotalValue = 1000000;
                e.blackTotalValue = -1000000;
                bestEvaluatedMoves.add(e);
            } catch (WhiteIsCheckMateException p_winningMove) {
                e.checkmate = true;
                e.whiteTotalValue = -1000000;
                e.blackTotalValue = 1000000;
                bestEvaluatedMoves.add(e);
                // break; // no need to look at the other moves
            }
            catch (EndOfGameException p_stalemate)
            {
                e.stalemate = true;
                e.whiteTotalValue = 0;
                e.blackTotalValue = 0;
                bestEvaluatedMoves.add(e);
            }
        }
        return bestEvaluatedMoves;
    }

    public int[] findBestWhiteMoves(int lookAhead, int movesToConsider, boolean multithreading) throws EndOfGameException {
        Comparator moveComparator = new WhiteMoveComparator();
        int[] moves = Arrays.copyOf(whiteMoves, numberOfWhiteMoves);
        try {
            List<XMove> evaluatedMoves = findBestMovesWithoutRecursion(moves, s_WHITE);
            if (evaluatedMoves.size() == 0) {
                if (isBlackKingThreatened)
                {
                    throw new BlackIsCheckMateException();
                }
                if (isWhiteKingThreatened)
                {
                	throw new WhiteIsCheckMateException();
                }
                
                throw new StaleMateException();
            }
            // elimitate silly moves
            if (lookAhead >= 0) {
                evaluatedMoves = findBestWhiteMovesRecursively(0, evaluatedMoves.size(), moveComparator, evaluatedMoves, multithreading);
            }
            List<XMove> bestEvaluatedMoves = new ArrayList<>();

            if (lookAhead > 0) {
                bestEvaluatedMoves = findBestWhiteMovesRecursively(lookAhead, movesToConsider - 1, moveComparator, evaluatedMoves, multithreading);
            } else {
                for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
                    XMove e = (XMove) evaluatedMoves.get(i);
                    bestEvaluatedMoves.add(e);
                }
            }
            int size = bestEvaluatedMoves.size();
//            if (0 == size)
//            {
//                System.err.println("No possible move left?");
//            }

            int[] bestMoves = new int[size];
            for (int i = 0; i < movesToConsider && i < size; i++) {
                bestMoves[i] = bestEvaluatedMoves.get(i).move;
            }
            return bestMoves;
        } catch (KingLostException p_checkmate) {
            throw new BlackIsCheckMateException();
        }
    }

    private List<XMove> findBestWhiteMovesRecursively(int lookAhead, int movesToConsider, Comparator moveComparator, List<XMove> evaluatedMoves, boolean multithreading) throws EndOfGameException {
        List<XMove> bestEvaluatedMoves;
        if (multithreading)
        {
            bestEvaluatedMoves = findBestWhiteMovesRecursivelyMultiThreaded(lookAhead, movesToConsider, evaluatedMoves);
        }
        else {
            bestEvaluatedMoves = findBestWhiteMovesRecursivelySingleThreaded(lookAhead, movesToConsider, evaluatedMoves);
        }
        Collections.sort(bestEvaluatedMoves, moveComparator);
        return bestEvaluatedMoves;
    }

    private List<XMove> findBestWhiteMovesRecursivelyMultiThreaded(final int lookAhead, final int movesToConsider, final List<XMove> evaluatedMoves) throws EndOfGameException {

        final List<XMove> bestEvaluatedMoves = new ArrayList<>();
        int proc = Runtime.getRuntime().availableProcessors();
        int mtc = 0;
        while (mtc<=movesToConsider)
        {
            mtc += proc;
        }
        ExecutorService executor = Executors.newFixedThreadPool(evaluatedMoves.size());
        try {
            List<Future<Object>> list = new ArrayList<Future<Object>>();
            for (int i = 0; i < evaluatedMoves.size() && i < mtc; i++) {
//                for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
                final XMove e = (XMove) evaluatedMoves.get(i);

                Callable<Object> worker = new Callable<Object>() {
                    public Object call() throws EndOfGameException {
                        try {
                            int opponentsMoveToConsider = movesToConsider;
                            if (lookAhead <= 0) {
                                opponentsMoveToConsider = 1;
                            }
                            int[] blackMoves = e.boardAfterMove.findBestBlackMoves(lookAhead - 1, opponentsMoveToConsider, false);
                            convertFirstMoveToXMove(e, whiteMoves);
                            synchronized (bestEvaluatedMoves) {
                                bestEvaluatedMoves.add(e);
                            }
                        } catch (WhiteIsCheckMateException p_impossibleMove) {
                            e.checkmate = true;
                            e.whiteTotalValue = -1000000;
                            e.blackTotalValue = 1000000;
                            bestEvaluatedMoves.add(e);
                        } catch (BlackIsCheckMateException p_winningMove) {
                            e.checkmate = true;
                            e.whiteTotalValue = 1000000;
                            e.blackTotalValue = -1000000;
                            bestEvaluatedMoves.add(e);
                        } catch (EndOfGameException p_stalemate) {
                            e.stalemate = true;
                            e.whiteTotalValue = 0;
                            e.blackTotalValue = 0;
                            bestEvaluatedMoves.add(e);
                        }
                        return null;
                    }
                };
                Future<Object> submit = executor.submit(worker);
                list.add(submit);

            }
            for (Future<Object> future : list) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            executor.shutdown();
        }
        return bestEvaluatedMoves;
    }


    private List<XMove> findBestWhiteMovesRecursivelySingleThreaded(int lookAhead, int movesToConsider, List<XMove> evaluatedMoves) throws EndOfGameException {
        List<XMove> bestEvaluatedMoves = new ArrayList<>();
        for (int i = 0; i < evaluatedMoves.size() && bestEvaluatedMoves.size() < movesToConsider; i++) {
            XMove e = (XMove) evaluatedMoves.get(i);
            try {
                int opponentsMoveToConsider = movesToConsider;
                if (lookAhead <= 0) {
                    opponentsMoveToConsider = 1;
                }
                int[] blackMoves = e.boardAfterMove.findBestBlackMoves(lookAhead - 1, opponentsMoveToConsider, false);
                convertFirstMoveToXMove(e, blackMoves);
                bestEvaluatedMoves.add(e);
            } catch (WhiteIsCheckMateException p_impossibleMove) {
                e.checkmate = true;
                e.whiteTotalValue = -1000000;
                e.blackTotalValue = 1000000;
                bestEvaluatedMoves.add(e);
            } catch (BlackIsCheckMateException p_winningMove) {
                e.checkmate = true;
                e.whiteTotalValue = 1000000;
                e.blackTotalValue = -1000000;
                bestEvaluatedMoves.add(e);
            } catch (EndOfGameException p_stalemate) {
                e.stalemate = true;
                e.whiteTotalValue = 0;
                e.blackTotalValue = 0;
                bestEvaluatedMoves.add(e);
            }
        }
        return bestEvaluatedMoves;
    }

    private void convertFirstMoveToXMove(XMove e, int[] whiteMoves) {
        if (null != whiteMoves && whiteMoves.length > 0) {
            int whiteMove = whiteMoves[0];
            int fromRow = (whiteMove >> 12) & 0x000F;
            int fromColumn = (whiteMove >> 8) & 0x000F;
            int toRow = (whiteMove >> 4) & 0x000F;
            int toColumn = whiteMove & 0x000F;
            int promotedPiece = (whiteMove >> 16) & 0x00FF;
            Chessboard afterWhiteMove = e.boardAfterMove.moveChessPiece(fromRow, fromColumn, toRow, toColumn, promotedPiece);
            e.whiteMaterialValue = afterWhiteMove.whiteMaterialValue;
            e.blackMaterialValue = afterWhiteMove.blackMaterialValue;
            e.whitePotentialMaterialValue = afterWhiteMove.whitePotentialMaterialValue;
            e.blackPotentialMaterialValue = afterWhiteMove.blackPotentialMaterialValue;
            e.whiteFieldPositionValue = afterWhiteMove.whiteFieldPositionValue;
            e.blackFieldPositionValue = afterWhiteMove.blackFieldPositionValue;
            e.whiteMoveValue = afterWhiteMove.whiteMoveValue;
            e.blackMoveValue = afterWhiteMove.blackMoveValue;
            e.whiteCoverageValue = afterWhiteMove.whiteCoverageValue;
            e.blackCoverageValue = afterWhiteMove.blackCoverageValue;
            e.isWhiteKingThreatened = afterWhiteMove.isWhiteKingThreatened;
            e.isBlackKingThreatened = afterWhiteMove.isBlackKingThreatened;
            e.whiteTotalValue = afterWhiteMove.whiteTotalValue;
            e.blackTotalValue = afterWhiteMove.blackTotalValue;
            e.numberOfWhiteMoves = afterWhiteMove.numberOfWhiteMoves;
            e.numberOfBlackMoves = afterWhiteMove.numberOfBlackMoves;
//            e.boardAfterMove = afterWhiteMove;
        }
    }

    protected List<XMove> findBestMovesWithoutRecursion(int[] moves, boolean p_activePlayerIsWhite) throws KingLostException {
        List<XMove> evaluatedMoves = new ArrayList<>(moves.length);
        for (int i = 0; i < moves.length; i++) {
            int move = moves[i];
//            int capturedValue = (move >> 24);
            int promotion = (move >> 16) & 0x00FF;
            int fromRow = (move >> 12) & 0x000F;
            int fromColumn = (move >> 8) & 0x000F;
            int toRow = (move >> 4) & 0x000F;
            int toColumn = move & 0x000F;
            Chessboard afterMove = new Chessboard(this, fromRow, fromColumn, toRow, toColumn, promotion);
            XMove e = new XMove();
            e.move = move;
            e.piece = getChessPiece(fromRow, fromColumn);
            e.whiteMaterialValue = afterMove.whiteMaterialValue;
            e.blackMaterialValue = afterMove.blackMaterialValue;
            e.whiteFieldPositionValue = afterMove.whiteFieldPositionValue;
            e.blackFieldPositionValue = afterMove.blackFieldPositionValue;
            e.whiteMoveValue = afterMove.whiteMoveValue;
            e.blackMoveValue = afterMove.blackMoveValue;
            e.whiteCoverageValue = afterMove.whiteCoverageValue;
            e.blackCoverageValue = afterMove.blackCoverageValue;
            e.isWhiteKingThreatened = afterMove.isWhiteKingThreatened;
            e.isBlackKingThreatened = afterMove.isBlackKingThreatened;
            e.whiteTotalValue = afterMove.whiteTotalValue;
            e.blackTotalValue = afterMove.blackTotalValue;
            e.numberOfWhiteMoves = afterMove.numberOfWhiteMoves;
            e.numberOfBlackMoves = afterMove.numberOfBlackMoves;
            e.boardAfterMove = afterMove;
            if (activePlayerIsWhite) {
                if (!afterMove.isWhiteKingThreatened) {
                    evaluatedMoves.add(e);
                }
            } else if (!afterMove.isBlackKingThreatened) {
                evaluatedMoves.add(e);
            }
            if ((e.whiteMaterialValue <= -9999) || (e.blackMaterialValue <= -9999)) {
                throw new KingLostException();
            }

        }
        if (p_activePlayerIsWhite)
            Collections.sort(evaluatedMoves, new WhiteMoveComparator());
        else
            Collections.sort(evaluatedMoves, new BlackMoveComparator());
        return evaluatedMoves;
    }

}
