package unalcol.agents.examples.sudoku.naive;
import unalcol.agents.search.*;
import unalcol.agents.examples.sudoku.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Kunsamu</p>
 *
 * @author Jonatan Gómez
 * @version 1.0
 */
public class NaiveBoardState implements State{
  protected int n = 0;
  protected Board board = null;
  public NaiveBoardState( SudokuPercept percept ) {
    n = SudokuLanguage.DIGITS;
    int[][] _board = new int[n][n];
    for( int i=0; i<n;i++){
        for( int j=0;j<n;j++){
          _board[i][j] = Integer.parseInt(percept.getAttribute ("<"+i+","+j+">").toString());
        }
    }
    board = new Board(_board);
  }

  public NaiveBoardState( NaiveBoardState source ){
    n = source.n;
    board = new Board( source.board );
  }

  public boolean equal( State other ){
    if( other instanceof NaiveBoardState ){
      NaiveBoardState other_state = (NaiveBoardState)other;
      boolean flag = true;
      for( int i=0; i<n && flag;i++){
          for( int j=0;j<n && flag;j++){
            flag = (board.get(i,j) == other_state.board.get(i,j));
          }
      }
      return flag;
    }
    return false;
  }
}
