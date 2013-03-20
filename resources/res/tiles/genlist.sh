#!/bin/sh
find . -name *.txt | sed -e 's/./res\/tiles/' > list
