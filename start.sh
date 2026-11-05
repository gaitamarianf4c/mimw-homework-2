#!/usr/bin/env bash
# Compiles the project and runs one of the apps, or all three.
#
#   ./start.sh                                   all three, one after another
#   ./start.sh log-router    [--log=PATH]
#   ./start.sh ip-insights   [--log=PATH] [--stat=PATH]
#   ./start.sh robot-factory [--config=PATH]

set -eo pipefail

# the three applications: command name and main class
readonly LOG_ROUTER_COMMAND="log-router"
readonly LOG_ROUTER_MAIN_CLASS="logrouter.LogRouterApplication"

readonly IP_INSIGHTS_COMMAND="ip-insights"
readonly IP_INSIGHTS_MAIN_CLASS="ipinsights.IpInsightsApplication"

readonly ROBOT_FACTORY_COMMAND="robot-factory"
readonly ROBOT_FACTORY_MAIN_CLASS="robotfactory.RobotFactoryApplication"

readonly ALL_COMMAND="all"

readonly SOURCES_DIR="src/main/java"
readonly CLASSES_DIR="target/classes"
readonly SOURCES_LIST="target/sources.txt"
readonly JAVA_RELEASE="21"

cd "$(dirname "$0")"

compile() {
    rm -rf "$CLASSES_DIR"
    mkdir -p "$CLASSES_DIR"
    find "$SOURCES_DIR" -name '*.java' > "$SOURCES_LIST"
    javac --release "$JAVA_RELEASE" -d "$CLASSES_DIR" @"$SOURCES_LIST"
}

run() {
    local main_class="$1"
    shift
    java -cp "$CLASSES_DIR" "$main_class" "$@"
}

compile

command_name="${1:-$ALL_COMMAND}"
if [ $# -gt 0 ]; then
    shift
fi

case "$command_name" in
    "$LOG_ROUTER_COMMAND")    run "$LOG_ROUTER_MAIN_CLASS" "$@" ;;
    "$IP_INSIGHTS_COMMAND")   run "$IP_INSIGHTS_MAIN_CLASS" "$@" ;;
    "$ROBOT_FACTORY_COMMAND") run "$ROBOT_FACTORY_MAIN_CLASS" "$@" ;;
    "$ALL_COMMAND")
        run "$LOG_ROUTER_MAIN_CLASS"
        run "$IP_INSIGHTS_MAIN_CLASS"
        run "$ROBOT_FACTORY_MAIN_CLASS"
        ;;
    *)
        echo "Usage: ./start.sh [$LOG_ROUTER_COMMAND|$IP_INSIGHTS_COMMAND|$ROBOT_FACTORY_COMMAND|$ALL_COMMAND] [--option=value ...]" >&2
        exit 1
        ;;
esac
