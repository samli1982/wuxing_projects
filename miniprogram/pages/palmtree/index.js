Page({
  data: {
    // 天干五行映射
    heavenlyStemWuxing: {
      '甲': '木', '乙': '木',
      '丙': '火', '丁': '火',
      '戊': '土', '己': '土',
      '庚': '金', '辛': '金',
      '壬': '水', '癸': '水'
    },
    // 地支五行映射
    earthlyBranchWuxing: {
      '子': '水', '丑': '土', '寅': '木', '卯': '木',
      '辰': '土', '巳': '火', '午': '火', '未': '土',
      '申': '金', '酉': '金', '戌': '土', '亥': '水'
    },
    // 五行颜色映射
    wuxingColors: {
      '木': '#07c160',  // 绿色
      '火': '#FF5E5E',  // 红色
      '土': '#FFC100',  // 黄色
      '金': '#4A90E2',  // 蓝色
      '水': '#000000'   // 黑色
    },
    // 十神关系映射（基于日干计算）
    tenGodsMap: {
      '甲': { '甲': '比肩', '乙': '劫财', '丙': '食神', '丁': '伤官', '戊': '偏财', '己': '正财', '庚': '偏官', '辛': '正官', '壬': '偏印', '癸': '正印' },
      '乙': { '甲': '劫财', '乙': '比肩', '丙': '伤官', '丁': '食神', '戊': '正财', '己': '偏财', '庚': '正官', '辛': '偏官', '壬': '正印', '癸': '偏印' },
      '丙': { '甲': '偏印', '乙': '正印', '丙': '比肩', '丁': '劫财', '戊': '食神', '己': '伤官', '庚': '偏财', '辛': '正财', '壬': '偏官', '癸': '正官' },
      '丁': { '甲': '正印', '乙': '偏印', '丙': '劫财', '丁': '比肩', '戊': '伤官', '己': '食神', '庚': '正财', '辛': '偏财', '壬': '正官', '癸': '偏官' },
      '戊': { '甲': '偏官', '乙': '正官', '丙': '偏印', '丁': '正印', '戊': '比肩', '己': '劫财', '庚': '食神', '辛': '伤官', '壬': '偏财', '癸': '正财' },
      '己': { '甲': '正官', '乙': '偏官', '丙': '正印', '丁': '偏印', '戊': '劫财', '己': '比肩', '庚': '伤官', '辛': '食神', '壬': '正财', '癸': '偏财' },
      '庚': { '甲': '偏财', '乙': '正财', '丙': '偏官', '丁': '正官', '戊': '偏印', '己': '正印', '庚': '比肩', '辛': '劫财', '壬': '食神', '癸': '伤官' },
      '辛': { '甲': '正财', '乙': '偏财', '丙': '正官', '丁': '偏官', '戊': '正印', '己': '偏印', '庚': '劫财', '辛': '比肩', '壬': '伤官', '癸': '食神' },
      '壬': { '甲': '食神', '乙': '伤官', '丙': '偏财', '丁': '正财', '戊': '偏官', '己': '正官', '庚': '偏印', '辛': '正印', '壬': '比肩', '癸': '劫财' },
      '癸': { '甲': '伤官', '乙': '食神', '丙': '正财', '丁': '偏财', '戊': '正官', '己': '偏官', '庚': '正印', '辛': '偏印', '壬': '劫财', '癸': '比肩' }
    },
    palmtrees: [],
    selectedId: '',
    fabX: 20,
    fabY: 0,
    nickname: '',
    birthInfo: '',
    eight: {
      year: { heavenly_stem: '庚', earthly_branch: '午' },
      month: { heavenly_stem: '辛', earthly_branch: '巳' },
      day: { heavenly_stem: '癸', earthly_branch: '酉' },
      hour: { heavenly_stem: '戊', earthly_branch: '午' }
    },
    nayin: '路旁土',
    kongwang: '戌亥',
    taiyuan: '壬申',
    wuxing: { 木: 18, 火: 32, 土: 20, 金: 25, 水: 5 },
    barWidth: { 木: '0%', 火: '0%', 土: '0%', 金: '0%', 水: '0%' },
    barWidthWood: '0%',
    barWidthFire: '0%',
    barWidthEarth: '0%',
    barWidthMetal: '0%',
    barWidthWater: '0%',
    wuxingWood: 0,
    wuxingFire: 0,
    wuxingEarth: 0,
    wuxingMetal: 0,
    wuxingWater: 0,
    usefulGods: ['水', '金'],
    wuyunliuqi: {
      year: 1990,
      transportation: '金运太过',
      tiansi: '少阴君火司天',
      zaiquan: '阳明燥金在泉',
      climate: '燥热偏盛，易伤肺津',
      health_warnings: ['肺金过盛，防呼吸系统不适', '心火受制，注意情绪低落']
    },
    constitution: '阳盛阴虚质',
    analysis: {
      cause: '火旺克金，水气不足，肝郁化火',
      symptom: '口干舌燥、失眠多梦、情绪急躁',
      tune: '滋阴降火，疏肝理气'
    },
    activeTab: 'diet',
    allTips: {
      diet: [
        { type: 'diet', text: '宜食黑色食物补肾（黑豆、海带），忌辛辣烧烤' }
      ],
      lifestyle: [
        { type: 'lifestyle', text: '建议晚10点前入睡，晨练面向东方吐纳' }
      ],
      emotion: [
        { type: 'emotion', text: '保持心境平和，可听羽调音乐安神' }
      ],
      herb: [
        { type: 'herb', text: '推荐麦冬泡水代茶饮，辅以酸枣仁安神' }
      ]
    },
    displayTips: []
  },
  onLoad() {
    // 检查登录状态，未登录则跳转到登录页
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    this.loadPalmtrees();
  },
  onShow() {
    // 每次页面显示时都检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    this.loadPalmtrees();
  },

  loadPalmtrees() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    // 从后端加载命盘列表
    const request = require('../../utils/request.js');
    request.get('/palmtree/list').then((res) => {
      const data = res.data || res;
      const list = Array.isArray(data) ? data : (data.records || []);
      console.log('👀 从后端加载命盘列表：', list.length, '条', list);
      
      if (list.length > 0) {
        // 映射数据结构：后端字段 -> 小程序字段
        const palmtrees = list.map(item => ({
          id: item.id,
          nickname: item.nickname || '未命名',
          year: item.birthYear,
          constitution_type: item.constitution || '未知',
          created_at: item.createTime
        }));
        
        const lastId = palmtrees[palmtrees.length - 1].id;
        this.setData({ 
          palmtrees: palmtrees, 
          selectedId: lastId
        }, () => {
          console.log('✅ 页面数据已更新， palmtrees:', this.data.palmtrees);
        });
        // 自动加载最后一个命盘的详细数据
        this.loadPalmtreeDetail(lastId);
      } else {
        console.log('⚠️ 命盘列表为空');
        this.setData({ palmtrees: [], selectedId: '' });
      }
    }).catch((err) => {
      console.error('❌ 加载命盘列表失败:', err);
      // 故障转移：仅从本地存储加载
      wx.getStorage({ 
        key: 'palmtrees', 
        success: (res) => {
          const list = res.data || [];
          console.log('👀 从本地加载命盘列表：', list.length, '条', list);
          if (list.length > 0) {
            const lastId = list[list.length - 1].id;
            this.setData({ 
              palmtrees: list, 
              selectedId: lastId
            }, () => {
              console.log('✅ 页面数据已更新， palmtrees:', this.data.palmtrees);
            });
            this.loadPalmtreeDetail(lastId);
          } else {
            console.log('⚠️ 命盘列表为空');
            this.setData({ palmtrees: [], selectedId: '' });
          }
        }, 
        fail: () => {
          console.log('❌ 未找到命盘数据，需要初始化');
          this.setData({ palmtrees: [], selectedId: '' });
        }
      });
    });
  },
  loadPalmtreeDetail(id) {
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const selected = this.data.palmtrees.find(p => p.id === id);
    if (!selected) return;
    
    // 优先从后端加载命盘详情
    const request = require('../../utils/request.js');
    request.get(`/palmtree/${id}`).then((res) => {
      const result = res.data || res;
      const palmtree = result.palmtree || result;
      const detail = result.detail || {};
      
      console.log('📦 后端返回数据:', palmtree);
      console.log('📦 命盘详情:', detail);
      
      // 构建显示数据
      const nick = palmtree.nickname || selected.nickname || '未命名';
      const hourNames = ['子','丑','寅','卯','辰','巳','午','未','申','酉','戌','亥'];
      
      // 优先使用birthHour/birthMinute，向下兼容birthHourIndex
      let hour, minute, hourLabel;
      if (palmtree.birthHour !== undefined && palmtree.birthHour !== null) {
        hour = palmtree.birthHour;
        minute = palmtree.birthMinute || 0;
        const hourIndex = (hour === 23) ? 0 : Math.floor((hour + 1) / 2);
        hourLabel = hourNames[hourIndex] || '子';
      } else {
        const hourIndex = palmtree.birthHourIndex !== undefined && palmtree.birthHourIndex !== null ? palmtree.birthHourIndex : 0;
        hourLabel = hourNames[hourIndex] || '子';
        hour = hourIndex * 2;
        minute = 0;
      }
      
      const year = palmtree.birthYear || selected.year || '未知';
      const month = palmtree.birthMonth || 1;
      const day = palmtree.birthDay || 1;
      const calendarType = palmtree.calendarType || 'gregorian';
      const calendarLabel = calendarType === 'lunar' ? '农历' : '公历';
      const city = palmtree.birthCity || '未知';
      
      // 解析命盘详情数据（从JSON字符串解析）
      let eightData = this.data.eight;
      let wuxingData = this.data.wuxing;
      let usefulGodsData = this.data.usefulGods;
      let wuyunliuqiData = this.data.wuyunliuqi;
      let constitutionData = this.data.constitution;
      let analysisData = this.data.analysis;
      let allTipsData = this.data.allTips;
      
      try {
        // 解析八字
        if (detail.yearHeavenlyStem) {
          const dayStem = detail.dayHeavenlyStem;
          eightData = {
            year: { 
              heavenly_stem: detail.yearHeavenlyStem, 
              earthly_branch: detail.yearEarthlyBranch,
              earthly_branch_tengod: this.getTenGodFromBranch(dayStem, detail.yearEarthlyBranch)
            },
            month: { 
              heavenly_stem: detail.monthHeavenlyStem, 
              earthly_branch: detail.monthEarthlyBranch,
              earthly_branch_tengod: this.getTenGodFromBranch(dayStem, detail.monthEarthlyBranch)
            },
            day: { 
              heavenly_stem: detail.dayHeavenlyStem, 
              earthly_branch: detail.dayEarthlyBranch,
              earthly_branch_tengod: this.getTenGodFromBranch(dayStem, detail.dayEarthlyBranch)
            },
            hour: { 
              heavenly_stem: detail.hourHeavenlyStem, 
              earthly_branch: detail.hourEarthlyBranch,
              earthly_branch_tengod: this.getTenGodFromBranch(dayStem, detail.hourEarthlyBranch)
            }
          };
          console.log('✅ 解析八字成功:', eightData);
        }
        
        // 解析五行
        if (detail.wuxingData) {
          const parsedWuxing = JSON.parse(detail.wuxingData);
          // 五行数据可能是整整数值或一个Map对象
          wuxingData = parsedWuxing.distribution || parsedWuxing || {木: 0, 火: 0, 土: 0, 金: 0, 水: 0};
          console.log('✅ 解析五行成功:', wuxingData);
        }
        
        // 解析喜用神
        if (detail.usefulGods) {
          const usefulGodsObj = JSON.parse(detail.usefulGods);
          usefulGodsData = usefulGodsObj.names || [];
          console.log('✅ 解析喜用神成功:', usefulGodsData);
        }
        
        // 解析五运六气
        if (detail.wuyunliuqiData) {
          wuyunliuqiData = JSON.parse(detail.wuyunliuqiData);
          wuyunliuqiData.year = year; // 确保年份正确
          console.log('✅ 解析五运六气成功:', wuyunliuqiData);
        }
        
        // 解析体质
        if (detail.constitutionType) {
          constitutionData = detail.constitutionType;
          console.log('✅ 解析体质成功:', constitutionData);
        }
        
        // 解析调理建议
        if (detail.adjustmentSuggestions) {
          const suggestions = JSON.parse(detail.adjustmentSuggestions);
          console.log('✅ 解析调理建议:', suggestions);
          
          // 提取体质分析
          if (suggestions.cause) {
            analysisData = {
              cause: suggestions.cause,
              symptom: suggestions.symptom,
              tune: suggestions.tune
            };
          }
          
          // 提取调理建议
          allTipsData = {
            diet: suggestions.diet || [],
            lifestyle: suggestions.lifestyle || [],
            emotion: suggestions.emotion || [],
            herb: suggestions.herb || []
          };
          console.log('✅ 调理建议分类:', allTipsData);
        }
        
      } catch (e) {
        console.error('❌ 解析命盘详情失败:', e);
      }
      
      // 计算真太阳时
      if (palmtree.birthCity) {
        const lng = palmtree.birthLng || 116.4074;
        this._calculateTrueSolarTime(year, month, day, hour, minute, lng, palmtree.birthCity, calendarLabel, hourLabel, {
          nickname: nick,
          eight: eightData,
          wuxing: wuxingData,
          usefulGods: usefulGodsData,
          wuyunliuqi: wuyunliuqiData,
          constitution: constitutionData,
          analysis: analysisData,
          allTips: allTipsData,
          nayin: detail.nayin || '路旁土',
          kongwang: detail.kongwang || '-',
          taiyuan: detail.taiyuan || '-'
        });
      } else {
        // 无城市信息，直接设置数据
        const birth = `${calendarLabel}：${year}年${month}月${day}日 ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}\n真太阳时间：${year}年${month}月${day}日 ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}（${city}）`;
        this.setData({
          nickname: nick,
          birthInfo: birth,
          eight: eightData,
          wuxing: wuxingData,
          usefulGods: usefulGodsData,
          wuyunliuqi: wuyunliuqiData,
          constitution: constitutionData,
          analysis: analysisData,
          allTips: allTipsData,
          nayin: detail.nayin || '路旁土',
          kongwang: detail.kongwang || '-',
          taiyuan: detail.taiyuan || '-',
          activeTab: 'diet'
        });
        this._calcBarWidth();
        this._updateTips();
      }
    }).catch((err) => {
      console.error('从后端加载命盘失败:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    });
  },

  _calculateTrueSolarTime(year, month, day, hour, minute, longitude, city, calendarLabel, hourLabel, dataObj) {
    const request = require('../../utils/request.js');
    const calendarType = dataObj.calendarType || 'gregorian';
    
    request.post('/true-solar-time/calculate', {
      year: year,
      month: month,
      day: day,
      hour: hour,
      minute: minute,
      longitude: longitude,
      city: city,
      calendar_type: calendarType
    }).then((res) => {
      const result = res.data || res;
      const trueSolar = result.trueSolar || {};
      const trueSolarHour = trueSolar.hour || hour;
      const trueSolarMinute = trueSolar.minute || minute;
      
      const birth = `${calendarLabel}：${year}年${month}月${day}日 ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}\n真太阳时间：${trueSolar.year}年${trueSolar.month}月${trueSolar.day}日 ${String(trueSolarHour).padStart(2, '0')}:${String(trueSolarMinute).padStart(2, '0')}（${city}）`;
      
      this.setData({
        nickname: dataObj.nickname || '未命名',
        birthInfo: birth,
        eight: dataObj.eight || this.data.eight,
        wuxing: dataObj.wuxing || this.data.wuxing,
        usefulGods: dataObj.usefulGods || [],
        wuyunliuqi: dataObj.wuyunliuqi || this.data.wuyunliuqi,
        constitution: dataObj.constitution || this.data.constitution,
        analysis: dataObj.analysis || this.data.analysis,
        allTips: dataObj.allTips || this.data.allTips,
        nayin: dataObj.nayin || '路旁土',
        kongwang: dataObj.kongwang || '-',
        taiyuan: dataObj.taiyuan || '-',
        activeTab: 'diet'
      });
      this._calcBarWidth();
      this._updateTips();
    }).catch((err) => {
      console.error('计算真太阳时失败:', err);
      // 失败时也设置数据（使用原始时间）
      let birth2 = `${calendarLabel}：${year}年${month}月${day}日 ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}\n真太阳时间：${year}年${month}月${day}日 ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}（${city}）`;
      this.setData({
        nickname: dataObj.nickname || '未命名',
        birthInfo: birth2,
        eight: dataObj.eight || this.data.eight,
        wuxing: dataObj.wuxing || this.data.wuxing,
        usefulGods: dataObj.usefulGods || [],
        wuyunliuqi: dataObj.wuyunliuqi || this.data.wuyunliuqi,
        constitution: dataObj.constitution || this.data.constitution,
        analysis: dataObj.analysis || this.data.analysis,
        allTips: dataObj.allTips || this.data.allTips,
        nayin: dataObj.nayin || '路旁土',
        kongwang: dataObj.kongwang || '-',
        taiyuan: dataObj.taiyuan || '-',
        activeTab: 'diet'
      });
      this._calcBarWidth();
      this._updateTips();
    });
  },

  _calcBarWidth() {
    const v = this.data.wuxing || {木: 0, 火: 0, 土: 0, 金: 0, 水: 0};
    const max = Math.max(v.木 || 0, v.火 || 0, v.土 || 0, v.金 || 0, v.水 || 0) || 1;
    const pct = (n) => `${Math.round(((n || 0) / max) * 100)}%`;
    this.setData({
      barWidth: { 木: pct(v.木), 火: pct(v.火), 土: pct(v.土), 金: pct(v.金), 水: pct(v.水) },
      barWidthWood: pct(v.木),
      barWidthFire: pct(v.火),
      barWidthEarth: pct(v.土),
      barWidthMetal: pct(v.金),
      barWidthWater: pct(v.水),
      wuxingWood: v.木 || 0,
      wuxingFire: v.火 || 0,
      wuxingEarth: v.土 || 0,
      wuxingMetal: v.金 || 0,
      wuxingWater: v.水 || 0
    });
  },
  _updateTips() {
    this.setData({ displayTips: this.data.allTips[this.data.activeTab] });
  },
  switchTab(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    this._updateTips();
  },
  onBarTap(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const name = e.currentTarget.dataset.name;
    const explain = {
      '木': '木弱：肝胆气不足，易情绪波动，可补木青色食物',
      '火': '火旺：易上火烦躁，宜滋阴降火',
      '土': '土偏：脾胃需调理，宜健脾祛湿',
      '金': '金偏：肺气盛或不足，注意呼吸道',
      '水': '水弱：肾虚畏寒，宜温补与保暖'
    }[name] || '五行说明';
    wx.showModal({ title: `${name}行释义`, content: explain, showCancel: false });
  },
  switchPalmtree(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    if (!id) {
      console.error('❌ 命盘ID为空，无法切换');
      wx.showToast({ title: '命盘ID错误', icon: 'none' });
      return;
    }
    
    console.log('🔄 切换命盘，目标ID:', id);
    this.setData({ selectedId: id });
    this.loadPalmtreeDetail(id);
    wx.showToast({ title: '命盘已切换', icon: 'none', duration: 1500 });
  },
  onCreate() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.navigateTo({ url: '/pages/palmtree/new' });
  },
  onEdit() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.showToast({ title: '编辑功能待实现', icon: 'none' });
  },
  onLongPress(e) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const id = e.currentTarget.dataset.id;
    wx.showActionSheet({
      itemList: ['重命名', '删除'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.showToast({ title: '重命名功能待实现', icon: 'none' });
        } else if (res.tapIndex === 1) {
          wx.showModal({ title: '确认删除', content: '删除后将无法恢复，是否继续？', success: (r) => {
            if (r.confirm) {
              let list = this.data.palmtrees.filter(p => p.id !== id);
              this.setData({ palmtrees: list });
              wx.setStorage({ key: 'palmtrees', data: list });
              wx.showToast({ title: '已删除', icon: 'success' });
            }
          }});
        }
      }
    });
  },
  onShowGodsTheory() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.showModal({ title: '喜用神原理', content: '喜用神是补益命局五行、调节阴阳平衡的关键。日主癸水弱，火旺克金，宜补水助金以平衡全局。', showCancel: false });
  },
  onSave() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const currentYear = new Date().getFullYear();
    
    const item = {
      id: `pt_${Date.now()}`,
      nickname: this.data.nickname || '未命名',
      year: this.data.wuyunliuqi?.year || currentYear,
      constitution_type: this.data.constitution || '未知',
      created_at: new Date().toISOString()
    };
    
    // 保存命盘列表
    wx.getStorage({ 
      key: 'palmtrees', 
      success: (res) => {
        const list = res.data || [];
        // 避免重复保存
        if (!list.find(p => p.nickname === item.nickname && p.year === item.year)) {
          list.push(item);
          wx.setStorage({ key: 'palmtrees', data: list });
          
          // 保存详细数据
          const detailData = {
            nickname: this.data.nickname,
            birth_year: this.data.wuyunliuqi?.year || currentYear,
            birth_month: 1,
            birth_day: 1,
            hour_index: 0,
            location_city: '未知城市',
            eight: this.data.eight,
            wuxing: this.data.wuxing,
            constitution: this.data.constitution,
            analysis: this.data.analysis,
            wuyunliuqi: this.data.wuyunliuqi
          };
          wx.setStorage({ key: `palmtree_${item.id}`, data: detailData });
          
          this.setData({ palmtrees: list, selectedId: item.id });
          wx.showToast({ title: '已保存', icon: 'success' });
        } else {
          wx.showToast({ title: '命盘已存在', icon: 'none' });
        }
      }, 
      fail: () => {
        const list = [item];
        wx.setStorage({ key: 'palmtrees', data: list });
        
        // 保存详细数据
        const detailData = {
          nickname: this.data.nickname,
          birth_year: this.data.wuyunliuqi?.year || currentYear,
          birth_month: 1,
          birth_day: 1,
          hour_index: 0,
          location_city: '未知城市',
          eight: this.data.eight,
          wuxing: this.data.wuxing,
          constitution: this.data.constitution,
          analysis: this.data.analysis,
          wuyunliuqi: this.data.wuyunliuqi
        };
        wx.setStorage({ key: `palmtree_${item.id}`, data: detailData });
        
        this.setData({ palmtrees: list, selectedId: item.id });
        wx.showToast({ title: '已保存', icon: 'success' });
      }
    });
  },
  onDetail() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    wx.navigateTo({ url: '/pages/learn/index' });
  },
  onShareAppMessage() {
    return {
      title: `${this.data.nickname}的命盘报告`,
      path: '/pages/palmtree/index'
    };
  },

  // 获取天干或地支的五行属性
  getWuxing(char, type) {
    if (type === 'stem') {
      return this.data.heavenlyStemWuxing[char] || '木';
    } else {
      return this.data.earthlyBranchWuxing[char] || '木';
    }
  },

  // 获取五行对应的颜色
  getWuxingColor(wuxing) {
    return this.data.wuxingColors[wuxing] || '#333333';
  },

  // 根据日干计算十神
  getTenGod(dayStem, targetStem) {
    const map = this.data.tenGodsMap[dayStem];
    return map ? map[targetStem] : '';
  },

  // 根据日干和地支计算十神（通过地支藏干）
  getTenGodFromBranch(dayStem, branch) {
    // 地支藏干映射（取主气）
    const branchHiddenStems = {
      '子': '癸', '丑': '己', '寅': '甲', '卯': '乙',
      '辰': '戊', '巳': '丙', '午': '丁', '未': '己',
      '申': '庚', '酉': '辛', '戌': '戊', '亥': '壬'
    };
    const hiddenStem = branchHiddenStems[branch];
    return this.getTenGod(dayStem, hiddenStem);
  }
});