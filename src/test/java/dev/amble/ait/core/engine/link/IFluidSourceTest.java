package dev.amble.ait.core.engine.link;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IFluidSourceTest {

    private static final double EPSILON = 0.000_001;

    @Test
    void insertionAndExtractionReportActualAmountsAndNotifyOnlyAtZero() {
        TestSource source = new TestSource(0, 100);

        assertEquals(100, source.insertLevel(125), EPSILON);
        assertEquals(100, source.level(), EPSILON);
        assertEquals(1, source.gainEvents);

        assertEquals(0, source.insertLevel(1), EPSILON);
        assertEquals(25, source.extractLevel(25), EPSILON);
        assertEquals(0, source.loseEvents);

        assertEquals(75, source.extractLevel(100), EPSILON);
        assertEquals(0, source.level(), EPSILON);
        assertEquals(1, source.loseEvents);
    }

    @Test
    void setterClampsAndInvalidTransferRequestsAreNoOps() {
        TestSource source = new TestSource(0, 100);

        source.setLevelAndNotify(250);
        assertEquals(100, source.level(), EPSILON);

        assertEquals(0, source.insertLevel(Double.POSITIVE_INFINITY), EPSILON);
        assertEquals(0, source.extractLevel(Double.NaN), EPSILON);
        assertEquals(100, source.level(), EPSILON);

        source.setLevelAndNotify(-10);
        assertEquals(0, source.level(), EPSILON);
    }

    @Test
    void legacyNotifyingSetterIsNotNotifiedTwice() {
        LegacySource source = new LegacySource();

        assertEquals(10, source.insertLevel(10), EPSILON);
        assertEquals(1, source.gainEvents);

        assertEquals(10, source.extractLevel(10), EPSILON);
        assertEquals(1, source.loseEvents);
    }

    private static final class TestSource implements IFluidSource {
        private double level;
        private final double maximum;
        private int gainEvents;
        private int loseEvents;

        private TestSource(double level, double maximum) {
            this.level = level;
            this.maximum = maximum;
        }

        @Override
        public double level() {
            return this.level;
        }

        @Override
        public void setLevel(double level) {
            this.level = level;
        }

        @Override
        public boolean setLevelNotifies() {
            return false;
        }

        @Override
        public double maxLevel() {
            return this.maximum;
        }

        @Override
        public void onGainFluid() {
            this.gainEvents++;
        }

        @Override
        public void onLoseFluid() {
            this.loseEvents++;
        }

        @Override
        public void setSource(IFluidSource source) {
        }

        @Override
        public void setLast(IFluidLink last) {
        }
    }

    private static final class LegacySource implements IFluidSource {
        private double level;
        private int gainEvents;
        private int loseEvents;

        @Override
        public double level() {
            return this.level;
        }

        @Override
        public void setLevel(double level) {
            double before = this.level;
            this.level = level;
            this.onChange(before, this.level);
        }

        @Override
        public double maxLevel() {
            return 100;
        }

        @Override
        public void onGainFluid() {
            this.gainEvents++;
        }

        @Override
        public void onLoseFluid() {
            this.loseEvents++;
        }

        @Override
        public void setSource(IFluidSource source) {
        }

        @Override
        public void setLast(IFluidLink last) {
        }
    }
}
