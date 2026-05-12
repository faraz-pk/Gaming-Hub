Write-Host "Compiling all games..." -ForegroundColor Cyan

# Snake Game
Write-Host "  Compiling Snake Game..." -ForegroundColor Yellow
Push-Location "Snake Game"
javac panels/GameFrame.java panels/GamePanel.java SnakeGameMain.java
Pop-Location

# Uno Game
Write-Host "  Compiling Uno Game..." -ForegroundColor Yellow
Push-Location "Uno Game"
javac UI/ImageLoader.java UI/Menu.java UI/AddPlayerNames.java UI/PickColorFrame.java UI/PopUp.java Logic/UnoCard.java Logic/UnoDeck.java Logic/Game.java Logic/GameStage.java Logic/InvalidColorSubmissionException.java Logic/InvalidPlayerTurnException.java Logic/InvalidValueSubmissionException.java Exceptions/InvalidColorSubmissionException.java Exceptions/InvalidPlayerTurnException.java Exceptions/InvalidValueSubmissionException.java UnoGameMain.java
Pop-Location

# Chess Game
Write-Host "  Compiling Chess Game..." -ForegroundColor Yellow
Push-Location "Chess Game"
javac Game/GameMode.java Game/GameState.java Util/Constants.java Util/ImageLoader.java Util/SoundPlayer.java Model/Tile.java Model/Move.java Model/Board.java Pieces/Piece.java Pieces/Pawn.java Pieces/Knight.java Pieces/Bishop.java Pieces/Rook.java Pieces/Queen.java Pieces/King.java Logic/MoveValidator.java Logic/CheckDetector.java Logic/GameController.java AI/ChessAi.java UI/TilePanel.java UI/BoardRenderer.java "UI/Panels/StartPanel.java" "UI/Panels/ModeSelectPanel.java" "UI/Panels/GamePanel.java" UI/GameFrame.java ChessGameMain.java
Pop-Location

# Sudoku Game
Write-Host "  Compiling Sudoku Game..." -ForegroundColor Yellow
Push-Location "Sudoku Game"
javac Controller/Difficulty.java Models/SudokuCell.java Models/SudokuBoard.java UI/SudokuGridPanel.java UI/NumberPadPanel.java Controller/SudokuController.java UI/SudokuFrame.java SudokuGameMain.java
Pop-Location

# Maze Game
Write-Host "  Compiling Maze Game..." -ForegroundColor Yellow
Push-Location "Maze Game"
javac Model/Player.java Model/Cell.java Model/Maze.java Logic/MazeGenerator.java Logic/GameEngine.java Controller/GameController.java UI/StartPanel.java UI/DifficultyPanel.java UI/GamePanel.java UI/MainFrame.java MazeGameMain.java
Pop-Location

# Tic Tac Toe Game
Write-Host "  Compiling Tic Tac Toe..." -ForegroundColor Yellow
Push-Location "Tic Tac Toe Game"
javac TicTacToe.java
Pop-Location

# Main Menu
Write-Host "  Compiling Main Menu..." -ForegroundColor Yellow
javac MainMenuGUI.java

Write-Host "Compilation complete! Launching Gaming Hub..." -ForegroundColor Green

# Run with ALL game directories on classpath
$sep = ";"
$cp = "." + $sep +
      "Snake Game" + $sep +
      "Uno Game" + $sep +
      "Uno Game/src" + $sep +
      "Chess Game" + $sep +
      "Sudoku Game" + $sep +
      "Maze Game" + $sep +
      "Tic Tac Toe Game"

java -cp $cp MainMenuGUI
