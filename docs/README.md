# KSQL Documentation

The full Confluent Platform documentation is available at [docs.confluent.io](https://docs.confluent.io/current/ksql/docs/index.html).

> *Important: This release is a **developer preview** and is free and open-source from Confluent under the Apache 2.0 license. Do not run KSQL against a production cluster.*

# Overview
KSQL is an open source streaming SQL engine that implements continuous, interactive queries against Apache Kafka™. It allows you to query, read, write, and process data in Apache Kafka in real-time, at scale using SQL commands. KSQL interacts directly with the [Kafka Streams API](https://kafka.apache.org/documentation/streams/), removing the requirement of building a Java app.

### Use cases
Common KSQL use cases are:

- Fraud detection - identify and act on out of the ordinary data to provide real-time awareness.
- Personalization - create real-time experiences and insight for end users driven by data.
- Notifications - build custom alerts and messages based on real-time data.
- Real-time Analytics - power real-time dashboards to understand what’s happening as it does.
- Sensor data and IoT - understand and deliver sensor data how and where it needs to be.
- Customer 360 - provide a clear, real-time understanding of your customers across every interaction.

KSQL lowers the barriers for using real-time data in your applications. It is powered by a scalable streaming platform without the learning curve or additional management complexity of other stream processing solutions.

## Modes of operation

You can use KSQL in standalone, client-server, application, and embedded modes. See [Concepts](/docs/concepts.md#concepts) for more information.

## Getting Started

* Beginners: Try the [interactive quick start](quickstart/index.rst). The quick start configures a single instance in a lightweight Docker container or in a Kafka cluster. It demonstrates a simple workflow using KSQL to write streaming queries against data in Kafka.
* Advanced users: Try the [end-to-end Clickstream Analysis demo](ksql-clickstream-demo/index.rst).

# Table of contents

- [Quick Start](quickstart/index.rst)
- [Configuring KSQL](config-ksql.rst)
- [Concepts](concepts.rst)
- [Syntax Reference](syntax-reference.rst)
- [Clickstream Analysis Demo](ksql-clickstream-demo/index.rst)
- [KSQL Examples](examples.rst)
- [Frequently Asked Questions](faq.rst)

# Building the documentation

This documentation is built using [Sphinx](http://sphinx-doc.org). It also uses some extensions for theming and REST API
documentation support.

Start by installing the requirements:

    pip install -r requirements.txt

Then you can generate the HTML version of the docs:

    make html

The root of the documentation will be at `_build/html/index.html`

While editing the documentation, you can get a live preview using python-livepreview. Install the Python library:

    pip install livereload

Then run the monitoring script in the background:

    python autoreload.py &

If you install the [browser extensions](http://livereload.com/) then everything should update every time any files are
saved without any manual steps on your part.
