#!/bin/bash
set -ex

echo "Starting reader-init.sh script"

# Wait for MySQL source to be ready
until mysqladmin ping -h"mysql-writer" -u"root" -p"$MYSQL_ROOT_PASSWORD" --silent; do
    echo "waiting for mysql-writer"
    sleep 5
done

echo "mysql-writer is ready"

echo "Checking connection to mysql-writer"
if ! mysql -h"mysql-writer" -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1; then
    echo "Failed to connect to mysql-writer"
    exit 1
fi
echo "Successfully connected to mysql-writer"

# Check binary logging status
echo "Checking binary logging status"
BINLOG_STATUS=$(mysql -h"mysql-writer" -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SHOW VARIABLES LIKE 'log_bin';" | awk 'NR==2 {print $2}')
echo "Binary logging status: $BINLOG_STATUS"

if [ "$BINLOG_STATUS" != "ON" ]; then
    echo "Binary logging is not enabled on mysql-writer"
    exit 1
fi

echo "Attempting to get SOURCE STATUS"
SOURCE_STATUS=$(mysql -h"mysql-writer" -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SHOW BINARY LOGS;" 2>/dev/null)
if [ -z "$SOURCE_STATUS" ]; then
    echo "BINARY LOGS is empty, initializing binary log"
    mysql -h"mysql-writer" -uroot -p"$MYSQL_ROOT_PASSWORD" -e "RESET MASTER;"
    SOURCE_STATUS=$(mysql -h"mysql-writer" -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SHOW BINARY LOGS;" 2>/dev/null)
fi

if [ -z "$SOURCE_STATUS" ]; then
    echo "Failed to initialize binary log"
    exit 1
fi

echo "SOURCE_STATUS output:"
echo "$SOURCE_STATUS"

CURRENT_LOG=$(echo "$SOURCE_STATUS" | awk 'NR==2 {print $1}')
CURRENT_POS=$(echo "$SOURCE_STATUS" | awk 'NR==2 {print $2}')

if [ -z "$CURRENT_LOG" ] || [ -z "$CURRENT_POS" ]; then
    echo "Failed to get source log file or position"
    exit 1
fi

echo "CURRENT_LOG: $CURRENT_LOG"
echo "CURRENT_POS: $CURRENT_POS"

# Set up replica
echo "Setting up replication"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<EOF
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='mysql-writer',
    SOURCE_USER='root',
    SOURCE_PASSWORD='$MYSQL_ROOT_PASSWORD',
    SOURCE_LOG_FILE='$CURRENT_LOG',
    SOURCE_LOG_POS=$CURRENT_POS;
START REPLICA;
SHOW REPLICA STATUS\G
EOF

echo "Replication setup completed"