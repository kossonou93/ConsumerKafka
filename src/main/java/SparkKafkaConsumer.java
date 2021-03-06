import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import kafka.serializer.StringDecoder;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

public class SparkKafkaConsumer {

    public static void main(String args[]) {
        System.out.println("Spark Streaming started now .....");

        SparkConf conf = new SparkConf()
                .setAppName("kafka-spark")
                .setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(20000));

        // TODO: processing pipeline

        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "localhost:9092");
        Set<String> topics = Collections.singleton("topic1");

        JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc,
                String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

        directKafkaStream.print();
        directKafkaStream.foreachRDD(rdd -> {
            System.out.println("--- Received new data RDD  " + rdd.partitions().size() + " partitions and " + rdd.count() + " records");
            rdd.foreach(record -> {
                System.out.println("Résultat données kafka-spark " + record._2);
            });
        });

        ssc.start();
        ssc.awaitTermination();

        System.out.println("Spark Streaming endin now .....");
    }
}
