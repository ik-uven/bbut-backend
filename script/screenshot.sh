#!/bin/bash

# ./screenshot.sh http://localhost:8080/results/women?hideMenu=true ./screens/results-women.png
# ./screenshot.sh http://localhost:8080/results/men?hideMenu=true ./screens/results-men.png
# ./screenshot.sh http://localhost:8080/results/teams?hideMenu=true ./screens/results-teams.png

TIMESTAMP=$(date +"%m_%d_%H_%M_%S")
RESULTS_URL="http://localhost:8080/results?hideMenu=true"
FILE_RESULTS="./screens/results-all.png"

[[ ! -z "$1" ]] && RESULTS_URL=$1
[[ ! -z "$2" ]] && FILE_RESULTS=$2

FILE_RESULTS=${FILE_RESULTS/.png/-${TIMESTAMP}.png}

/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --headless --window-size=1280,1440 --hide-scrollbars --screenshot="$FILE_RESULTS" "$RESULTS_URL";

exit 0
