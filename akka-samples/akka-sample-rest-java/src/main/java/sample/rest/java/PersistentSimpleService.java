/**
 * Copyright (C) 2009-2010 Scalable Solutions AB <http://scalablesolutions.se>
 */

package sample.rest.java;

import se.scalablesolutions.akka.actor.annotation.transactionrequired;
import se.scalablesolutions.akka.actor.annotation.prerestart;
import se.scalablesolutions.akka.actor.annotation.postrestart;
import se.scalablesolutions.akka.persistence.common.PersistentMap;
import se.scalablesolutions.akka.persistence.cassandra.CassandraStorage;

import java.nio.ByteBuffer;

@transactionrequired
public class PersistentSimpleService {
  private String KEY = "COUNTER";

  private boolean hasStartedTicking = false;
  private PersistentMap<byte[], byte[]> storage;

  public String count() {
    if (storage == null) storage = CassandraStorage.newMap();
    if (!hasStartedTicking) {
      storage.put(KEY.getBytes(), ByteBuffer.allocate(4).putInt(0).array());
      hasStartedTicking = true;
      return "Tick: 0\n";
    } else {
      byte[] bytes = (byte[])storage.get(KEY.getBytes()).get();
      int counter = ByteBuffer.wrap(bytes).getInt();
      storage.put(KEY.getBytes(), ByteBuffer.allocate(4).putInt(counter + 1).array());
      return "Tick: " + counter + "\n";
    }
  }

  @prerestart
  public void preRestart() {
    System.out.println("Prepare for restart by supervisor");
  }

  @postrestart
  public void postRestart() {
    System.out.println("Reinitialize after restart by supervisor");
  }
}