/*
package dev.amble.ait.data.schema.console.type;


public class HudolinType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/hudolin");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.16249998f, 0.31249997f),
                    new Vector3f(-0.48203126061707735f, 0.48750004917383194f, 0.5136718945577741f)),
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.15f, 0.18750001f),
                    new Vector3f(0.5609371401369572f, 0.5000000968575478f, -0.6234378945082426f)),
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.1375f, 0.1875f),
                    new Vector3f(0.5476560769602656f, 0.4624997144564986f, 0.5855467021465302f)),
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.18750001f, 0.15f),
                    new Vector3f(-0.7726568207144737f, 0.48749980982393026f, 0.14140605833381414f)),
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.1125f, 0.1625f),
                    new Vector3f(-0.6148439403623343f, 0.5999997165054083f, 0.13554669544100761f)),
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.33749995f, 0.2125f),
                    new Vector3f(-0.5523443222045898f, 0.46249966602772474f, -0.33671894017606974f)),
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.1f, 0.125f),
                    new Vector3f(0.32656249310821295f, 0.5000002393499017f, -0.7976558804512024f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.29999998f, 0.3749999f),
                    new Vector3f(-0.0015630722045898438f, 0.40001535415649414f, 0.6378898620605469f)),
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.099999994f, 0.1375f),
                    new Vector3f(-0.11093711853027344f, 0.5374999046325684f, -0.7875003814697266f)),
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.15000004f, 0.099999994f),
                    new Vector3f(-0.7499998146668077f, 0.46250023879110813f, 0.7859376929700375f)),
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.1125f, 0.15f),
                    new Vector3f(-0.32265588268637657f, 0.47750005684792995f, -0.7675783270969987f)),
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.275f, 0.15f),
                    new Vector3f(0.399218556471169f, 0.7000002423301339f, 0.22656270302832127f)),
            new ControlTypes(new XControl(), EntityDimensions.changing(0.1375f, 0.125f),
                    new Vector3f(-0.8726564589887857f, 0.44999980740249157f, 0.20390587765723467f)),
            new ControlTypes(new YControl(), EntityDimensions.changing(0.15f, 0.1f), new
                    Vector3f(-0.7484371336176991f, 0.4750002846121788f, 0.45195352006703615f)),
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.125f, 0.1f),
                    new Vector3f(-0.6359377028420568f, 0.4874998079612851f, 0.6882812678813934f)),
            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.099999994f, 0.3749999f),
                    new Vector3f(0.26093693170696497f, 0.31251535285264254f, 0.7003898629918694f)),
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.1375f, 0.16250001f),
                    new Vector3f(-1.0531250098720193f, 0.39999942295253277f, 0.23867206927388906f)),
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.15f, 0.16250001f),
                    new Vector3f(-0.5328123075887561f, 0.5875003831461072f, 0.31328105833381414f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.17500001f, 0.06249999f),
                    new Vector3f(-0.06015586946159601f, 0.5249998094514012f, 0.8140623103827238f)),
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.125f, 0.20000002f),
                    new Vector3f(0.1023441357538104f, 0.40000033378601074f, 0.8398439669981599f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.15f, 0.125f),
                    new Vector3f(0.79140645544976f, 0.5374998087063432f, -0.14804707467556f)),
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.15f, 0.16250001f),
                    new Vector3f(-0.6328121200203896f, 0.49999961722642183f, 0.3757820138707757f)),
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.1125f, 0.17500001f),
                    new Vector3f(0.6757814390584826f, 0.47500028647482395f, 0.3320316392928362f)),
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.049999997f, 0.16250001f),
                    new Vector3f(-0.5468752104789019f, 0.46249980945140123f, 0.6648441385477781f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.20000002f, 0.16250001f),
                    new Vector3f(0.4890621304512024f, 0.6125006694346666f, -0.2843749960884452f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.32499996f, 0.15f),
                    new Vector3f(-7.816189900040627E-4f, 0.7500003390014172f, -0.3578126961365342f)),
            new ControlTypes(new MarkWaypointControl(), EntityDimensions.changing(0.17500001f, 0.1f),
                    new Vector3f(0.08750039339065552f, 0.5250003822147846f, -0.8335935743525624f)),
            new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.20000002f, 0.16250001f),
                    new Vector3f(0.6390623115003109f, 0.4625006653368473f, -0.3718751920387149f)),
            new ControlTypes(new SetWaypointControl(), EntityDimensions.changing(0.16250001f, 0.1125f),
                    new Vector3f(0.11406230926513672f, 0.5750004760921001f, -0.6726560592651367f)),
            new ControlTypes(new SonicPortControl(),EntityDimensions.changing(0.21250002f, 0.15f),
                    new Vector3f(-1.0101562598720193f, 0.44999999552965164f, -0.23671875055879354f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.07499999f, 0.1375f),
                    new Vector3f(0.7414058679714799f, 0.48749985732138157f, 0.20195274520665407f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.099999994f, 0.2625f),
                    new Vector3f(-0.46484432090073824f, 0.549999619834125f, -0.27421855833381414f)),
    };

    public HudolinType() {
        super(REFERENCE, "hudolin");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.HUDOLIN;
    }
}
*/
