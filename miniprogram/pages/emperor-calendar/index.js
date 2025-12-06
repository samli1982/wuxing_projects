import * as homeApi from '../../api/home.js';

Page({
  data: {
    statusBarHeight: 44,
    _updateTag: Date.now(), // 版本控制标记，确保每次都是最新的代码
    selectedDate: {
      solarDate: '2025-11-25',
      weekday: '星期二'
    },
    lunarInfo: {
      lunarDate: '十月初六',
      stem: '',
      branch: '',
      nayin: ''
    },
    ganzhiInfo: {
      year: '乙巳',
      zodiac: '蛇',
      month: '丁亥',
      day: '戊戌',
      yearSound: '平地木',
      monthSound: '屋上土',
      daySound: '平地木'
    },
    yijiInfo: {
      yi: '',
      ji: ''
    },
    detailInfo: {
      nayin: '',
      chongsha: '',
      zhishen: '',
      jianzhu: '',
      jishen: '',
      taishen: '',
      xiongshen: '',
      xingxiu: '',
      pengzu: '',
      duty: '',
      twelveStar: '',
      twentyEightStar: ''
    },
    shiChenList: []
  },
  onLoad(options) {
    try {
      const sys = wx.getWindowInfo();
      if (sys && sys.statusBarHeight) {
        this.setData({ statusBarHeight: sys.statusBarHeight });
      }
    } catch (e) {}
    
    console.log('🔥 页面加载，onLoad事件执行了');
    console.log('■■■ 强制刷新标志:', this.data._updateTag);

    // 加载今日的干支纪年历信息
    this.loadCalendarData();
  },
  loadCalendarData() {
    const today = new Date();
    this.loadEmperorCalendarInfo(today);
  },
  loadEmperorCalendarInfo(date) {
    // 格式化日期
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const dateStr = `${year}-${month}-${day}`;

    // 更新选中日期
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const weekday = weekdays[date.getDay()];
    
    this.setData({
      selectedDate: {
        solarDate: dateStr,
        weekday: weekday
      }
    });

    // 1. 获取干支历信息
    homeApi.getSixtiesCycleInfo(dateStr).then(data => {
      console.log('干支历信息:', data);
      this.setData({
        ganzhiInfo: {
          year: data.year,
          zodiac: this.getZodiac(data.year),
          month: data.month,
          day: data.day,
          yearSound: data.yearSound || '',
          monthSound: data.monthSound || '',
          daySound: data.daySound || ''
        }
      });
    }).catch(err => {
      console.warn('加载干支历失败:', err);
    });

    // 2. 获取农历信息
    homeApi.getLunarInfo(dateStr).then(data => {
      console.log('农历信息:', data);
      this.setData({
        lunarInfo: {
          lunarDate: data.lunarDate,
          stem: data.stem || '',
          branch: data.branch || '',
          nayin: data.nayin || ''
        }
      });
    }).catch(err => {
      console.warn('加载农历失败:', err);
    });

    // 3. 获取宜忌信息
    homeApi.getEmperorCalendarYiJi(dateStr).then(data => {
      console.log('🔥 宜忌信息API返回:', data);
      // 用完整对象替换，确保绑定能正确更新
      this.setData({
        yijiInfo: {
          yi: data.yi,
          ji: data.ji
        }
      }, () => {
        console.log('✅ yijiInfo已更新:', this.data.yijiInfo);
      });
    }).catch(err => {
      console.warn('❌ 加载宜忌信息失败:', err);
      this.setData({
        yijiInfo: {
          yi: '合帐 裁衣 嫁娶 安床 入殓 移柩 破土 造畜稠',
          ji: '置产 造船 开光 掘井 作灶'
        }
      });
    });

    // 4. 并行加载两个详细信息API，然后合并更新
    Promise.all([
      homeApi.getSixtyCycleDayInfo(dateStr),
      homeApi.getEmperorCalendarDetail(dateStr)
    ]).then(([sixtyCycleDayData, detailData]) => {
      console.log('🔥 干支日详细信息API返回:', sixtyCycleDayData);
      console.log('🔥 详细信息API返回:', detailData);
      
      // 构建新的detailInfo对象
      const newDetailInfo = {
        duty: sixtyCycleDayData.duty || '未知',
        twelveStar: sixtyCycleDayData.twelveStar || '未知',
        twentyEightStar: sixtyCycleDayData.twentyEightStar || '未知',
        daySound: sixtyCycleDayData.daySound || '未知',
        nayin: detailData.nayin || '未知',
        chongsha: detailData.chongsha || '未知',
        zhishen: detailData.zhishen || '未知',
        jianzhu: detailData.jianzhu || '未知',
        jishen: detailData.jishen || '未知',
        taishen: detailData.taishen || '未知',
        xiongshen: detailData.xiongshen || '未知',
        xingxiu: detailData.xingxiu || '未知',
        pengzu: detailData.pengzu || '未知'
      };
      
      // 用完整对象替换，确保绑定能正确更新
      this.setData({ detailInfo: newDetailInfo }, () => {
        console.log('✅ detailInfo已更新:', this.data.detailInfo);
      });
    }).catch(err => {
      console.warn('❌ 加载详细信息失败:', err);
      this.setData({
        detailInfo: {
          duty: '未知',
          twelveStar: '未知',
          twentyEightStar: '未知',
          daySound: '平地木',
          nayin: '平地木',
          chongsha: '冲龙 煞北',
          zhishen: '金匮(吉)',
          jianzhu: '闭',
          jishen: '益后 金匮',
          taishen: '房床栖 房内中',
          xiongshen: '月煞 月虚 血支 五虚 绝阳',
          xingxiu: '室火猪 吉',
          pengzu: '戊不受田田主不祥 戌不吃犬作怪上床'
        }
      });
    });
    
    // 更新时辰吃冶
    this.updateShiChenList(dateStr);
  },
  getZodiac(ganzhiYear) {
    // 根据天干地支年份获取生肖
    const zodiacs = ['鼠', '牛', '虎', '兔', '龙', '蛇', '马', '羊', '猴', '鸡', '狗', '猪'];
    const earthBranches = ['子', '丑', '寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥'];
    
    // 从干支年份中提取地支（最后一个字）
    const lastChar = ganzhiYear.charAt(ganzhiYear.length - 1);
    const branchIndex = earthBranches.indexOf(lastChar);
    
    if (branchIndex !== -1) {
      return zodiacs[branchIndex];
    }
    return '';
  },
  // 根据干支日期更新时辰吉凶信息
  updateShiChenList(dateStr) {
    homeApi.getShiChenInfo(dateStr).then(data => {
      console.log('🔥 时辰吉凶信息API返回:', data);
      if (data && data.shiChenList) {
        this.setData({ shiChenList: data.shiChenList }, () => {
          console.log('✅ shiChenList已更新:', this.data.shiChenList);
        });
      }
    }).catch(err => {
      console.warn('❌ 加载时辰吉凶信息失败:', err);
      // 加载失败时，使用硬编码的默认值
    });
  },
  onPrevDay() {
    const dateStr = this.data.selectedDate.solarDate;
    const [year, month, day] = dateStr.split('-').map(Number);
    const currentDate = new Date(year, month - 1, day);
    const prevDate = new Date(currentDate.getTime() - 24 * 60 * 60 * 1000);
    this.loadEmperorCalendarInfo(prevDate);
  },
  onNextDay() {
    const dateStr = this.data.selectedDate.solarDate;
    const [year, month, day] = dateStr.split('-').map(Number);
    const currentDate = new Date(year, month - 1, day);
    const nextDate = new Date(currentDate.getTime() + 24 * 60 * 60 * 1000);
    this.loadEmperorCalendarInfo(nextDate);
  },
  onSelectDate() {
    // 打开日期选择器
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    
    wx.chooseDate({
      solarLunar: 1,
      success: (res) => {
        console.log('选择日期:', res);
        // 解析选中的日期
        let year, month, day;
        if (res.lunar) {
          const selectedDateStr = this.convertLunarToSolar(res.year, res.month, res.day);
          const [y, m, d] = selectedDateStr.split('-').map(Number);
          year = y;
          month = m;
          day = d;
        } else {
          year = res.year;
          month = res.month;
          day = res.day;
        }
        const selectedDate = new Date(year, month - 1, day);
        this.loadEmperorCalendarInfo(selectedDate);
      },
      fail: (err) => {
        console.warn('日期选择失败:', err);
      }
    });
  },
  convertLunarToSolar(lunarYear, lunarMonth, lunarDay) {
    // 这里应该调用后端API将农历转换为公历
    // 暂时返回当前日期
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
  }
});
