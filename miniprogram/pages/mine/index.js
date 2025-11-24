Page({
  data: {
    themeMode: 'dark',
    userInfo: {
      nickname: '子时居士',
      avatar_color: '#E63946',
      consecutive_days: 7,
      total_palmtrees: 3,
      last_updated: '2025-04-08T14:22:11Z'
    },
    userStats: {
      total_palmtrees: 3,
      last_palmtree: {
        name: '张三（1990）',
        created_at: '2025-04-08',
        constitution: '阴虚质'
      },
      notes_count: 12,
      favorites: {
        courses: 5,
        herbs: 3,
        palmtrees: 2
      },
      learning_duration: '4h 22min',
      learning_progress: [
        {
          course_id: 'course_001',
          title: '《五运六气详解》',
          progress: 60
        },
        {
          course_id: 'course_002',
          title: '《药精分类入门》',
          progress: 30
        }
      ]
    }
  },

  onLoad() {
    console.log('✅ 我的页面已加载');
    this.loadUserData();
  },

  onPullDownRefresh() {
    console.log('🔄 下拉刷新...');
    this.loadUserData();
    setTimeout(() => {
      wx.stopPullDownRefresh();
      wx.showToast({ title: '已同步', icon: 'success', duration: 1500 });
    }, 1000);
  },

  loadUserData() {
    // 从本地存储加载用户数据
    wx.getStorage({
      key: 'user_info',
      success: (res) => {
        if (res.data) {
          this.setData({ userInfo: res.data });
        }
      }
    });
  },

  onProfileEdit() {
    wx.navigateTo({ url: '/pages/mine/profile' });
  },

  onQuickAction(e) {
    const action = e.currentTarget.dataset.action;
    const actionMap = {
      palmtrees: '/pages/palmtree/list',
      favorites: '/pages/mine/favorites',
      notes: '/pages/learn/notes',
      history: '/pages/learn/history',
      backup: 'backup',
      theme: 'theme',
      service: 'service',
      guide: 'guide'
    };

    const target = actionMap[action];

    if (action === 'backup') {
      wx.showActionSheet({
        itemList: ['导出为 JSON', '导出为 CSV', '云端备份'],
        success: (res) => {
          wx.showToast({ title: '导出功能待实现', icon: 'none' });
        }
      });
    } else if (action === 'theme') {
      this.onToggleTheme();
    } else if (action === 'service') {
      wx.showToast({ title: '客服功能待实现', icon: 'none' });
    } else if (action === 'guide') {
      wx.navigateTo({ url: '/pages/mine/guide' });
    } else if (target.startsWith('/')) {
      wx.navigateTo({ url: target });
    }
  },

  onViewAllPalmtrees() {
    wx.navigateTo({ url: '/pages/palmtree/list' });
  },

  onViewNotes() {
    wx.navigateTo({ url: '/pages/learn/notes' });
  },

  onContinueLearn(e) {
    const courseId = e.currentTarget.dataset.courseId;
    wx.navigateTo({ url: `/pages/learn/course?id=${courseId}` });
  },

  onToggleTheme() {
    const newTheme = this.data.themeMode === 'dark' ? 'light' : 'dark';
    this.setData({ themeMode: newTheme });
    wx.setStorage({
      key: 'app_theme',
      data: newTheme
    });
    wx.showToast({ title: `已切换为${newTheme === 'dark' ? '暗黑' : '亮色'}模式`, icon: 'none' });
  },

  onOpenSettings(e) {
    const type = e.currentTarget.dataset.type;
    const settingsMap = {
      privacy: '/pages/mine/privacy',
      notifications: '/pages/mine/notifications',
      backup: '/pages/mine/backup',
      about: 'about',
      feedback: 'feedback',
      guide: '/pages/mine/guide'
    };

    const target = settingsMap[type];

    if (type === 'about') {
      wx.showModal({
        title: '关于我们',
        content: '时空本草学派 · 天人合一助手\n\n版本号：v1.0.0\n\n融合中医经典理论、天文历法与五运六气的智能养生助手',
        showCancel: false
      });
    } else if (type === 'feedback') {
      wx.navigateTo({ url: '/pages/mine/feedback' });
    } else if (target.startsWith('/')) {
      wx.navigateTo({ url: target });
    }
  },

  onShareAppMessage() {
    return {
      title: '时空本草学派 · 天人合一助手',
      path: '/pages/mine/index'
    };
  },

  onLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？退出后需要重新登录才能使用应用。',
      confirmText: '退出',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          // 清除本地所有数据
          wx.clearStorage();
          
          // 显示退出成功提示
          wx.showToast({
            title: '已退出登录',
            icon: 'success',
            duration: 1500
          });

          // 延迟后返回首页
          setTimeout(() => {
            wx.redirectTo({ url: '/pages/home/index' });
          }, 1500);
        }
      }
    });
  }
});
