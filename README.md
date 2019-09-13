[![Build Status](https://travis-ci.org/ratosh/pirarucu.svg?branch=master)](https://travis-ci.org/ratosh/pirarucu)

# Pirarucu

A Kotlin Chess Engine with Universal Chess Interface protocol support and more than 3000 CCRL Rating.

## Results

- [Computer Chess Rating Lists 40/4](http://www.computerchess.org.uk/ccrl/404/cgi/compare_engines.cgi?family=Pirarucu&print=Rating+list&print=Results+table&print=LOS+table&print=Ponder+hit+table&print=Eval+difference+table&print=Comopp+gamenum+table&print=Overlap+table&print=Score+with+common+opponents)
- [Computer Chess Rating Lists 40/40](http://www.computerchess.org.uk/ccrl/4040/cgi/compare_engines.cgi?family=Pirarucu&print=Rating+list&print=Results+table&print=LOS+table&print=Ponder+hit+table&print=Eval+difference+table&print=Comopp+gamenum+table&print=Overlap+table&print=Score+with+common+opponents)

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
- Static Exchange Evaluation
- Quiescence search
- Alpha-beta search
    - Aspiration window
    - Interactive deepening
    - Null move reduction
    - Razoring pruning
    - Futility pruning
    - Late Move Reduction
    - Late move pruning
    - History futility pruning
- Evaluation
    - Material value
    - Piece square table
    - Tapered evaluation
    - Mobility
    - Pawn evaluation
    - Passed pawn evaluation
    - King safety
    - Safe check
- Move Ordering
    - Most value victim / Lowest Value Attacker
    - Killer moves
    - Butterfly history
- Multi Thread
    
## How to use

### Requirements

The engine runs on JRE (Java Runtime Environment) version 8 and above.

###  Running

- Download the latest [release](https://github.com/ratosh/pirarucu/releases/latest).
- Uncompress the downloaded file in a empty directory.
- Run the bash file in bin directory. 

### Compatible Interfaces

- [Arena](http://www.playwitharena.com)
- [Cutechess](https://github.com/cutechess/cutechess)

## Contributions

You are welcome to contribute, please follow the [instructions](CONTRIBUTING.md).

## Appreciation

- To all who share ideas and/or code, without this huge resource I would not be able to make such strong engine.
- To all who tested Pirarucu, especially to all CCRL team who are constantly testing several engines (including several versions of Pirarucu).
- To Andrew Grant for developing [OpenBench](https://github.com/AndyGrant/OpenBench), a great open source tool to test and track progress of chess engines. I started using it just after v2.2.2 release and recommend it for every author.
- To all who shared their computer power with the Pirarucu OpenBench instance.

## Author

- Raoni Campos

## Supported By

- [JetBrains](https://www.jetbrains.com/?from=pirarucu)
