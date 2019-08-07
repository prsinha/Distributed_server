# Distributed_server

A Highly Available Simplified Distributed Banking System

High Availability through Primary Backup Replication (Passive Replication)

In this project high-availability is achieved through passive or Primary-backup replication. The Backup or
“warm standby” is a Branch Server that is running in the background (normally on a different machine),
receiving requests from the primary server to update its state and hence ready to jump in if the primary server
fails. Thus, when the primary server receives a request from a client which will change its state, it performs the
request, sends the state update request to the backup server and sends the reply back to the client (while the
state update may not yet have been done at the backup server). Sending update only for requests that
change state help reduce the number of unnecessary messages in the system and thus enhance performance.
Since the primary and backup servers are usually on a local area network, they communicate using the
unreliable UDP protocol. However, the communication between them should be reliable and FIFO.


Primary server receives requests from clients as CORBA invocations, performs the request, sends the state
update request to the backup server using UDP data grams if necessary, and sends the response back to the
client (while the state update may not yet have been done at the backup server). When the primary notices
that the backup does not respond within a reasonable time, it assumes that the backup has failed and informs
the Branch Monitor so that a new backup server can be created and initialized.
