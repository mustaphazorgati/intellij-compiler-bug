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

function main() {
  [[ "$1" == '-h' || "$1" == '--help' ]] && helpAndExit 0
  [[ $# -eq 0 ]] && helpAndExit 1
  [[ "$(dirname "$0")" != "." ]] && echo "You have to execute this script from the same directory" && exit 1
  . .utils.sh
  
  jdks=()

  for dir in "$@"; do
    if [[ -d "$dir" ]]; then
      while read -r possibleJdkPath; do 
        if [[ -f "$possibleJdkPath/bin/java" ]]; then
          jdks+=( "$possibleJdkPath" )
        fi
      done < <(find "$dir" -maxdepth 1 -type d | sort)
    fi
  done

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
