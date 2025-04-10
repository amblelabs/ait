package dev.drtheo.autojson;

import com.google.gson.Gson;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
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

        long autoObj2Str = System.currentTimeMillis() - start;
        System.out.println("autojson (obj->str): " + autoObj2Str);

        start = System.currentTimeMillis();

        for (int i = 0; i < iters; i++) {
            gson.toJson(bean);
        }

        long gsonObj2Str = System.currentTimeMillis() - start;
        System.out.println("gson (obj->str): " + gsonObj2Str);

        System.out.println("RESULT: autojson vs gson: " + -(1-((float) autoObj2Str/gsonObj2Str))*100f + "%");

        //

        String raw = """
                {"primInt":2147483647,"primBool":true,"primByte":127,"primChar":"￿","primShort":32767,"primDouble":1.7976931348623157E308,"primFloat":3.4028235E38,"primLong":9223372036854775807, "intObj":2147483647,"boolObj":true,"byteObj":127,"charObj":"￿","shortObj":32767,"doubleObj":1.7976931348623157E308,"floatObj":3.4028235E38,"longObj":9223372036854775807,"hello":"HELLO MATE","id":{"namespace":"ait","path":"whatever"}}""";


        start = System.currentTimeMillis();

        for (int i = 0; i < iters; i++) {
            adapter.fromJson(raw, Bean.class);
        }

        long autoStr2Obj = System.currentTimeMillis() - start;
        System.out.println("autojson (str->obj): " + autoStr2Obj);

        start = System.currentTimeMillis();

        for (int i = 0; i < iters; i++) {
            gson.fromJson(raw, Bean.class);
        }

        long gsonStr2Obj = System.currentTimeMillis() - start;
        System.out.println("gson (str->obj): " + gsonStr2Obj);

        System.out.println("RESULT: autojson vs gson: " + -(1-((float) autoStr2Obj/gsonStr2Obj))*100f + "%");
    }
}
