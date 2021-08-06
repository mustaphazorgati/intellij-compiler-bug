#!/bin/bash

##constants
FAILED='\033[0;31m'
SUCCESS='\033[0;34m'

# Arguments:
#   $1: length of first column
#   $2: length of second column
#   $3: first column value
#   $4: second colum value
function printRow() {
  printf "$(determineRowColor "$4")%-${1}s | %-${2}s\n" "$3" "$4"
}

# Arguments:
#  $1: compiles value
function determineRowColor() {
  [[ "$1" == 'yes' ]] && echo "$SUCCESS"
  [[ "$1" == 'no' || "$1" == 'other error' ]] && echo "$FAILED"
}

# Arguments:
#   $1: maven log
function mavenLogContainsError() {
  if [[ "$1" == *"unreported exception org.example.exception.TestException; must be caught or declared to be thrown"* ]]; then
    echo TRUE
  else
    echo FALSE
  fi
}

function getLongestJdkLength() {
  longestJdkNameLength=0
  for jdk in "$@"; do
    length=$(basename "$jdk" | sed -z 's/\n//'  | wc -m)
    [[ $length -gt $longestJdkNameLength ]] && longestJdkNameLength=$length
  done
  echo "$longestJdkNameLength"
}

function getMaxLength() {
  max=0
  for i in "$@"; do
    length=$(sed -z 's/\n//' <<< "$i"| wc -m) 
    [[ $length -gt $max ]] && max=$length
  done
  echo "$max"
}


