[![Build Status](https://travis-ci.org/ratosh/pirarucu.svg?branch=master)](https://travis-ci.org/ratosh/pirarucu)

# Pirarucu

A Kotlin Chess Engine with UCI protocol support.

## Concepts

- Color represents chess colors.
- Piece represents chess piece type.
- Bitboard represents a board (each signed bit indicates a piece on that location).
- Square represents a board position square index.
- File represents a board file index.
- Rank represents a board rank index.

## Features

- Bitboard representation
- Magic bitboard
- Transposition table
- Quiescence search
    - Static Exchange Evaluation
- Alpha-beta search
    - Aspiration window
    - Interactive deepening
    - Null move reduction
    - Razoring pruning
    - Futility pruning
    - Late Move Reduction
    - Killer moves
- Evaluation
    - Piece square table
    - Tapered evaluation
    - Mobility
    - Pawn evaluation
    - King safety
    
## How to use

### Requirements

The engine runs on JRE (Java Runtime Environment) version 8 and above.

###  Running

- Download the latest [release](https://github.com/ratosh/pirarucu/releases/latest).
- Uncompress the downloaded file in a empty directory.
- Run the bash file in bin directory. 

## Contributions

You are welcome to contribute, please follow the [instructions](CONTRIBUTING.md).

## Results

- Version 2.0.2 was 4th place on [69th Amateur Series Division 8](http://kirill-kryukov.com/chess/discussion-board/viewtopic.php?f=7&t=10026&sid=a4b2cffa89f8762ea0ef0458d6676d42)
- Computer Chess Rating Lists [CCRL 40/4](http://www.computerchess.org.uk/ccrl/404/cgi/compare_engines.cgi?family=Pirarucu&print=Rating+list&print=Results+table&print=LOS+table&print=Ponder+hit+table&print=Eval+difference+table&print=Comopp+gamenum+table&print=Overlap+table&print=Score+with+common+opponents)
- Computer Chess Rating Lists [CCRL 40/40](http://www.computerchess.org.uk/ccrl/4040/cgi/compare_engines.cgi?family=Pirarucu&print=Rating+list&print=Results+table&print=LOS+table&print=Ponder+hit+table&print=Eval+difference+table&print=Comopp+gamenum+table&print=Overlap+table&print=Score+with+common+opponents)

## Author

- Raoni Campos
