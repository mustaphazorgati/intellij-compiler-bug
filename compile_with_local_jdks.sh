#!/bin/bash


#H Usage:
#H %FILE% -h | %FILE% --help
#H
#H prints this help and exits
#H
#H %FILE% <JDK Base directory> [JDK Base direcotry...]
#H
#H   Iterates over every JDK in 'JDK Base directory' and compiles this project
#H
#H Requirements:
#H   JDK Base directory - the directory where your JDK is in. IMPORTANT: This is not the jdk directory
# Arguments:
#   $1: exit code
function helpAndExit() {
  filename=$(basename "$0")
  grep "^#H" < "$0" | cut -c4- | sed -e "s/%FILE%/$filename/g"
  exit "$1"
}

##constants
RED='\033[0;31m'
GREEN='\033[0;32m'

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
  [[ "$1" == 'yes' ]] && echo "$GREEN"
  [[ "$1" == 'no' || "$1" == 'other error' ]] && echo "$RED"
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

# Arguments:
#   $1: Bash Array of Strings
function getLongestJdkLength() {
  longestJdkNameLength=0
  for jdk in "${jdks[@]}"; do
    length=$(basename "$jdk" | sed -z 's/\n//'  | wc -m)
    [[ $length -gt $longestJdkNameLength ]] && longestJdkNameLength=$length
  done
  echo "$longestJdkNameLength"
}

function main() {
  [[ "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  [[ $# -eq 0 ]] && helpAndExit 1
  
  jdks=()

  for dir in "$@"; do
    if [[ -d "$dir" ]]; then
      while read -r possibleJdkPath; do 
        if [[ -f "$possibleJdkPath/bin/java" ]]; then
          jdks+=( "$possibleJdkPath" )
        fi
      done < <(find "$dir" -maxdepth 1 -type d)
    fi
  done

  # sort array
  IFS=$'\n' jdks=($(sort <<<"${jdks[*]}")); unset IFS

  echo detected JDKs:
  for jdk in "${jdks[@]}"; do
    echo "$jdk"
  done

  echo 
  read -p "Continue? [Y/n]" -n 1 -r
  echo
  [[ -n "$REPLY" && ! "$REPLY" =~ ^[Yy]$  ]] && exit 0

  longestJdkNameLength=$(getLongestJdkLength "${jdks[@]}")

  printRow "$longestJdkNameLength" 20 JDK compiles?
  for jdk in "${jdks[@]}"; do
    log=$(JAVA_HOME="$jdk" "$(dirname "$0")"/mvnw clean compile 2>&1)
    mvn_success=$?
    contains_error=$(mavenLogContainsError "$log")

    if [[ "$mvn_success" -eq 0 ]]; then
      compiles=yes
    elif [[ "$contains_error" == "TRUE" ]]; then
      compiles=no
    else
      compiles="other error"
    fi

    printRow "$longestJdkNameLength" 20 "$(basename "$jdk")" "$compiles" 
  done
}

main "$@"
