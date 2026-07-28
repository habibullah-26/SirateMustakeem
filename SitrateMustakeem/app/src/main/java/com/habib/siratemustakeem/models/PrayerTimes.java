package com.habib.siratemustakeem.models;

    import java.util.List;

    public class PrayerTimes {
        private List<Prayer> prayers;

        public PrayerTimes(List<Prayer> prayers) {
            this.prayers = prayers;
        }

        public Prayer getNextPrayer() {
            // Placeholder logic for determining the next prayer
            return prayers.get(0); // Assuming the list is not empty
        }
    }