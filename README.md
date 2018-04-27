[![Build Status](https://travis-ci.org/ratosh/pirarucu.svg?branch=master)](https://travis-ci.org/ratosh/pirarucu)

Pirarucu
========

A Kotlin Chess Engine with UCI protocol support.

Concepts
========

- Color represents chess colors.
- Piece represents chess piece type.
- Bitboard represents a board (each signed bit indicates a piece on that location).
- Square represents a board position square index.
- File represents a board file index.
- Rank represents a board rank index.

Features
========

- Bitboard representation
- Magic bitboard
- Transposition table
- Quiescence search
    - Static Exchange Evaluation
- Alpha-beta search
    - Aspiration window
    - Interactive deepening
    - Null move reductions
    - Razoring pruning
    - Futility pruning
- Evaluation
    - Piece square table
    - Tapered evaluation

Contributions
=============

You are welcome to contribute. Create an issue to discuss a feature before sending a Pull Request.

Author
======

- Raoni Campos