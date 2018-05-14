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
    
## How to use

### Requirements

The engine runs on JRE (Java Runtime Environment) version 8 and above.

###  Running

- Download the latest [release](https://github.com/ratosh/pirarucu/releases/latest).
- Uncompress the downloaded file in a empty directory.
- Run the bash file in bin directory. 

## Contributions

You are welcome to contribute, please follow the [instructions](CONTRIBUTING.md).

## Author

- Raoni Campos
