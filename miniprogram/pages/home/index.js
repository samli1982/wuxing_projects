Page({
  data: {
    statusBarHeight: 44,
    headerInfo: {
      sunriseTime: '05:28',
      lunarDate: '乙巳年四月初八',
      solarTermName: '立夏',
      solarTermDayIndex: 3,
      beidouDirection: '斗柄东南 · 天地火气渐升'
    },
    wuyun: {
      year: 2025,
      ganzhi: '乙巳年',
      nayin: '覆灯火',
      sitian: '厥阴风木',
      zaiquan: '少阳相火',
      healthTips: [
        '肝木受克，注意情绪波动',
        '肺金偏旺，防呼吸道不适',
        '宜养阴润燥，忌辛辣燥热',
        '午后适度运动，避免暴晒'
      ]
    },
    showAllTips: false,
    hasPalmtree: false,
    latestPalmtreeSummary: '',
    healthTipVisible: true,
    healthTipText: '您四柱木弱，今值金运当令，建议佩戴绿植饰品调和。'
  },
  onLoad() {
    try {
      const sys = wx.getSystemInfoSync();
      if (sys && sys.statusBarHeight) {
        this.setData({ statusBarHeight: sys.statusBarHeight });
      }
    } catch (e) {}

    // 读取健康提示关闭缓存
    wx.getStorage({
      key: 'health_tip_closed',
      success: (res) => {
        if (res && res.data) {
          this.setData({ healthTipVisible: false });
        }
      }
    });

    // 读取最近命盘摘要
    wx.getStorage({
      key: 'latest_palmtree_summary',
      success: (res) => {
        if (res && res.data) {
          this.setData({ hasPalmtree: true, latestPalmtreeSummary: res.data });
        }
      },
      fail: () => {
        this.setData({ hasPalmtree: false, latestPalmtreeSummary: '' });
      }
    });
  },
  toggleTips() {
    const showAll = !this.data.showAllTips;
    this.setData({ showAllTips: showAll });
  },
  onShowSolarTerm() {
    wx.showModal({
      title: '节气详情',
      content: `${this.data.headerInfo.solarTermName}·第${this.data.headerInfo.solarTermDayIndex}天\n天地火气渐升，宜养阴润燥。`,
      showCancel: false
    });
  },
  goWuYunDetail() {
    wx.navigateTo({ url: '/pages/learn/index' });
  },
  onCreatePalmtree() {
    wx.navigateTo({ url: '/pages/palmtree/new' });
  },
  onViewPalmtree() {
    if (this.data.hasPalmtree) {
      wx.navigateTo({ url: '/pages/palmtree/list' });
    } else {
      wx.showToast({ title: '您尚未创建命盘，请先添加', icon: 'none' });
    }
  },
  onCloseHealthTip() {
    wx.setStorage({ key: 'health_tip_closed', data: Date.now() });
    this.setData({ healthTipVisible: false });
  },
  observers: {
    'wuyun.healthTips, showAllTips': function (tips, showAll) {
      const display = showAll ? tips : tips.slice(0, 3);
      this.setData({ displayTips: display });
    }
  }
});
