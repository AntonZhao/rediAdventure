package com.anton.Chapter1;

import java.util.HashMap;
import java.util.Map;

public class FunnelRateLimiter {

    private Map<String, Funnel> funnels = new HashMap<>();

    public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingTRate) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        Funnel funnel = funnels.get(key);
        if (funnel == null) {
            funnel = new Funnel(capacity, leakingTRate);
            funnels.put(key, funnel);
        }
        return funnel.watering(1);
    }

    /**
     * capacity -> 漏斗容量
     * leakingRate -> 漏嘴流水速率
     * leftQuota -> 漏斗剩余空间
     * leakingTs -> 上次漏水时间
     */
    static class Funnel {
        int capacity;
        float leakingRate;
        int leftQuota;
        long lastLeakingTime;

        public Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.lastLeakingTime = System.currentTimeMillis();
        }

        void makeSpace() {
            long nowTime = System.currentTimeMillis();
            // 距离上次漏水多久
            long deltaTime = nowTime - lastLeakingTime;
            // 又可以腾出多少空间
            int deltaQuota = (int) (deltaTime / 1000 * leakingRate);

            // 间隔时间太长，数字太大溢出
            if (deltaQuota < 0) {
                this.leftQuota = capacity;
                this.lastLeakingTime = nowTime;
                return;
            }
            //腾出空间数字太小，最小单位是1，等下次吧
            if (deltaQuota < 1) {
                return;
            }

            this.leftQuota += deltaQuota;
            this.lastLeakingTime = nowTime;
            if (this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
        }

        boolean watering(int quota) {
            makeSpace();
            System.out.println("容量 = " + this.capacity + " 流出速率 = " + this.leakingRate + " 上次流出时间 = " + this.lastLeakingTime + " 剩余容量 = " + this.leftQuota);
            if (this.leftQuota > quota) {
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }
}
