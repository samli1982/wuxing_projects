-- 查看最新的命盘详情
SELECT 
  pd.id,
  pd.palmtree_id,
  pd.year_heavenly_stem,
  pd.month_heavenly_stem,
  pd.day_heavenly_stem,
  pd.hour_heavenly_stem,
  pd.kongwang,
  pd.taiyuan,
  pd.nayin
FROM palmtree_detail pd
ORDER BY pd.id DESC
LIMIT 5;
