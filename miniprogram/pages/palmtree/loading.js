Page({
  data: {
    startTime: 0
  },
  onLoad() {
    const start = Date.now();
    this.setData({ startTime: start });
    // 模拟后端推算耗时（800ms ~ 2800ms）
    this.simulateCompute().then(() => {
      const elapsed = Date.now() - this.data.startTime;
      if (elapsed < 1500) {
        // 很快返回，直接进入结果页，避免闪屏
        wx.redirectTo({ url: '/pages/palmtree/result' });
      } else if (elapsed >= 1500 && elapsed < 2500) {
        // 显示至少 2.5s
        const wait = 2500 - elapsed;
        setTimeout(() => wx.redirectTo({ url: '/pages/palmtree/result' }), wait);
      } else {
        // 超过 2.5s，立即跳转
        wx.redirectTo({ url: '/pages/palmtree/result' });
      }
    });
  },
  simulateCompute() {
    return new Promise((resolve) => {
      const t = Math.floor(800 + Math.random() * 2000);
      setTimeout(resolve, t);
    });
  }
});
