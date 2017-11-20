.. _ksql_concepts:

Concepts
========

==========
Components
==========

The main components of KSQL are the KSQL CLI and the KSQL server.

KSQL CLI
--------

The KSQL CLI allows you to interactively write KSQL queries. Its
interface should be familiar to users of MySQL, Postgres, Oracle, Hive,
Presto, etc.

The KSQL CLI acts as a client to the KSQL server.

KSQL Server
-----------

The KSQL server runs the engine that executes KSQL queries, which
includes the data processing as well as reading data from and writing
data to the target Kafka cluster.

===========
Terminology
===========

When using KSQL, the following terminology is used.

Stream
------

A stream is an unbounded sequence of structured data (“facts”). For
example, we could have a stream of financial transactions such as “Alice
sent $100 to Bob, then Charlie sent $50 to Bob”. Facts in a stream are
immutable, which means new facts can be inserted to a stream, but
existing facts can never be updated or deleted. Streams can be created
from a Kafka topic or derived from existing streams and tables. In both
cases, a stream’s underlying data is durably stored (persisted) within a
Kafka topic on the Kafka brokers.

Table
-----

A table is a view of a stream, or another table, and represents a
collection of evolving facts. For example, we could have a table that
contains the latest financial information such as “Bob’s current account
balance is $150”. It is the equivalent of a traditional database table
but enriched by streaming semantics such as windowing. Facts in a table
are mutable, which means new facts can be inserted to the table, and
existing facts can be updated or deleted. Tables can be created from a
Kafka topic or derived from existing streams and tables. In both cases,
a table’s underlying data is durably stored (persisted) within a Kafka
topic on the Kafka brokers.

.. _modes-of-operation:

==================
Modes of operation
==================

Standalone mode
---------------

In stand-alone mode, both the KSQL client and server components are
co-located on the same machine, in the same JVM, and are started
together. This makes standalone mode very convenient for local
development and testing.

.. image:: img/standalone-mode.png

To run KSQL in standalone mode:

-  Start the KSQL CLI and the server components all in the same JVM:

   -  Start with default settings:

      .. code:: bash

		$ ./bin/ksql-cli local

   -  Start with :ref:`custom
      settings <configuring-ksql>`, pointing
      KSQL at a specific Kafka cluster (see the Streams
      `bootstrap.servers <streams_developer-guide_required-configs>`
      setting):

      .. code:: bash

		$ ./bin/ksql-cli local --bootstrap-server kafka-broker-1:9092 \
                       --properties-file path/to/ksql-cli.properties

Client-server mode
------------------

In client-server mode, you can run a pool of KSQL servers on remote
machines, VMs, or containers. The CLI then connects to these remote KSQL
servers over HTTP.

.. image:: img/client-server.png

To run KSQL in client-server mode:

-  Start any number of server nodes:

   -  Start with default settings:

      .. code:: bash

		$ ./bin/ksql-server-start

   -  Start with :ref:`custom
      settings <configuring-ksql>`, pointing
      KSQL at a specific Kafka cluster (see Streams :ref:`bootstrap servers <streams_developer-guide_required-configs>` setting):

      .. code:: bash

         $ hostname
         my-ksql-server

         $ cat ksql-server.properties
         # You must set at least the following two properties
         bootstrap.servers=kafka-broker-1:9092
         # Note: `application.id` is not really needed but you must set it
         #       because of a known issue in the KSQL Developer Preview
         application.id=app-id-setting-is-ignored
         
         # Optional settings below, only for illustration purposes
         # The hostname/port on which the server node will listen for client connections
         listeners=http://0.0.0.0:8090

      To start the server node with the settings above:

      .. code:: bash

		$ ./bin/ksql-server-start ksql-server.properties

-  Start any number of CLIs, specifying the desired KSQL server address
   as the ``remote`` endpoint:

   .. code:: bash

       $ ./bin/ksql-cli remote http://my-ksql-server:8090

All KSQL servers (and their engines) share the work of processing KSQL
queries that are submitted to them:

- To add processing capacity, start more KSQL servers (scale out). You can do this during live operations.
- To remove processing capacity, stop some of the running KSQL servers. You can do this during live operations. The remaining KSQL servers will automatically take over the processing work of the stopped servers. Make sure that at least one KSQL server is running, otherwise your queries will not be executed any longer.
