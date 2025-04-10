package dev.drtheo.autojson;

import com.google.gson.Gson;
import dev.drtheo.autojson.adapter.JsonStringAdapter;
import dev.drtheo.autojson.bake.UnsafeUtil;

public class Bean {
    private int primInt = Integer.MAX_VALUE;
    private boolean primBool = true;
    private byte primByte = Byte.MAX_VALUE;
    private char primChar = Character.MAX_VALUE;
    private short primShort = Short.MAX_VALUE;
    private double primDouble = Double.MAX_VALUE;
    private float primFloat = Float.MAX_VALUE;
    private long primLong = Long.MAX_VALUE;

    private Integer intObj = Integer.MAX_VALUE;
    private Boolean boolObj = true;
    private Byte byteObj = Byte.MAX_VALUE;
    private Character charObj = Character.MAX_VALUE;
    private Short shortObj = Short.MAX_VALUE;
    private Double doubleObj = Double.MAX_VALUE;
    private Float floatObj = Float.MAX_VALUE;
    private Long longObj = Long.MAX_VALUE;

    private String hello = "HELLO MATE";
    private Id id = new Id("ait", "whatever");
    //private Sound sound = new Sound(id);

    static class Id {
        private final String namespace;
        private final String path;

        public Id(String namespace, String path) {
            this.namespace = namespace;
            this.path = path;
        }
    }

    record Sound(Id id) {

    }

    @Override
    public String toString() {
        return "Bean{" +
                "primInt=" + primInt +
                ", primBool=" + primBool +
                ", primByte=" + primByte +
                ", primChar=" + primChar +
                ", primShort=" + primShort +
                ", primDouble=" + primDouble +
                ", primFloat=" + primFloat +
                ", primLong=" + primLong +
                ", intObj=" + intObj +
                ", boolObj=" + boolObj +
                ", byteObj=" + byteObj +
                ", charObj=" + charObj +
                ", shortObj=" + shortObj +
                ", doubleObj=" + doubleObj +
                ", floatObj=" + floatObj +
                ", longObj=" + longObj +
                ", hello='" + hello + '\'' +
                ", id=" + id +
                //", sound=" + sound +
                '}';
    }

    public static void main(String[] args) throws NoSuchFieldException {
        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        Gson gson = new Gson();
        Bean bean = new Bean();

        long start;
        final int iters = 1_000_000;

        UnsafeUtil.warmup();

        start = System.currentTimeMillis();

        for (int i = 0; i < iters; i++) {
            adapter.toJson(bean);
        }

        System.out.println("autojson: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();

        for (int i = 0; i < iters; i++) {
            gson.toJson(bean);
        }

        System.out.println("gson: " + (System.currentTimeMillis() - start));
    }
}
