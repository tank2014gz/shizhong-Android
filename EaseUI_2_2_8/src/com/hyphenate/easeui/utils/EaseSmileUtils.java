/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;

public class EaseSmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";
    
//	public static final String ee_1 = "[):]";
//	public static final String ee_2 = "[:D]";
//	public static final String ee_3 = "[;)]";
//	public static final String ee_4 = "[:-o]";
//	public static final String ee_5 = "[:p]";
//	public static final String ee_6 = "[(H)]";
//	public static final String ee_7 = "[:@]";
//	public static final String ee_8 = "[:s]";
//	public static final String ee_9 = "[:$]";
//	public static final String ee_10 = "[:(]";
//	public static final String ee_11 = "[:'(]";
//	public static final String ee_12 = "[:|]";
//	public static final String ee_13 = "[(a)]";
//	public static final String ee_14 = "[8o|]";
//	public static final String ee_15 = "[8-|]";
//	public static final String ee_16 = "[+o(]";
//	public static final String ee_17 = "[<o)]";
//	public static final String ee_18 = "[|-)]";
//	public static final String ee_19 = "[*-)]";
//	public static final String ee_20 = "[:-#]";
//	public static final String ee_21 = "[:-*]";
//	public static final String ee_22 = "[^o)]";
//	public static final String ee_23 = "[8-)]";
//	public static final String ee_24 = "[(|)]";
//	public static final String ee_25 = "[(u)]";
//	public static final String ee_26 = "[(S)]";
//	public static final String ee_27 = "[(*)]";
//	public static final String ee_28 = "[(#)]";
//	public static final String ee_29 = "[(R)]";
//	public static final String ee_30 = "[({)]";
//	public static final String ee_31 = "[(})]";
//	public static final String ee_32 = "[(k)]";
//	public static final String ee_33 = "[(F)]";
//	public static final String ee_34 = "[(W)]";
//	public static final String ee_35 = "[(D)]";
//    public static final String ee_1 = "[::)]";//微笑
//	public static final String ee_2 = "[::~]";//撇嘴
//	public static final String ee_3 = "[::B]";//色
//	public static final String ee_4 = "[::\\]";//发呆
//	public static final String ee_5 = "[:8-)]";//得意
//	public static final String ee_6 = "[::<]";//流泪
//	public static final String ee_7 = "[::$]";//害羞
//	public static final String ee_8 = "[::X]";//闭嘴
//	public static final String ee_9 = "[::Z]";//睡
////	public static final String ee_10 = "[::'(]";//大哭
////	public static final String ee_11 = "[::-|]";//尴尬
//	public static final String ee_12 = "[::@]";//发怒
//	public static final String ee_13 = "[::P]";//调皮
//	public static final String ee_14 = "[::D]";//呲牙
//	public static final String ee_15 = "[::O]";//惊讶
//	public static final String ee_16 = "[::(]";//难过
////	public static final String ee_17 = "[::+]";//酷
////	public static final String ee_18 = "[:-b]";//冷汗
////	public static final String ee_19 = "[::Q]";//抓狂
////	public static final String ee_20 = "[::T]";//吐
////	public static final String ee_21 = "[:,@P]";//偷笑
//	public static final String ee_22 = "[:,@-D]";//愉快
//	public static final String ee_23 = "[::d]";//白眼
//	public static final String ee_24 = "[:,@o]";//傲慢
////	public static final String ee_25 = "[::g]";//饥饿
//	public static final String ee_26 = "[:|-)]";//困
//	public static final String ee_27 = "[::!]";//惊恐
//	public static final String ee_28 = "[::L]";//流汗
//	public static final String ee_29 = "[::>]";//憨笑
////	public static final String ee_30 = "[::,@]";//悠闲
////	public static final String ee_31 = "[:,@f]";//奋斗
////	public static final String ee_32 = "[::-s]";//咒骂
////	public static final String ee_33 = "[:？]";//疑问
////	public static final String ee_34 = "[:,@x]";//嘘
//	public static final String ee_35 = "[:,@@]";//晕
//	public static final String ee_36 = "[::8]";//疯啦
////	public static final String ee_37 = "[:,@!]";//衰
////	public static final String ee_38 = "[:!!!]";//骷髅
////	public static final String ee_39 = "[:xx]";//敲打
//	public static final String ee_40 = "[:bye]";//再见
//	public static final String ee_41 = "[:wipe]";//擦汗
////	public static final String ee_42 = "[:dig]";//抠鼻
//	public static final String ee_43 = "[:handclap]";//鼓掌
////	public static final String ee_44 = "[:&-(]";//糗大了
//	public static final String ee_45 = "[:B-)]";//坏
////	public static final String ee_46 = "[:<@]";//左哼哼
////	public static final String ee_47 = "[:@>]";//右哼哼
////	public static final String ee_48 = "[::-O]";//哈欠
////	public static final String ee_49 = "[:>-|]";//鄙视
////	public static final String ee_50 = "[:P-(]";//委屈
//	public static final String ee_51 = "[::'|]";//快哭了
//	public static final String ee_52 = "[:x-)]";//阴险
//	public static final String ee_53 = "[:::*]";//亲亲
////	public static final String ee_54 = "[:@x]";//吓
////	public static final String ee_55 = "[:8*]";//可怜
////	public static final String ee_56 = "[:pd]";//菜刀
////	public static final String ee_57 = "[:<W>]";//西瓜
////	public static final String ee_58 = "[:bear]";//啤酒
////	public static final String ee_59 = "[:basketb]";//篮球
////	public static final String ee_60 = "[:oo]";//兵乓
////	public static final String ee_61 = "[:coffee]";//咖啡
////	public static final String ee_62 = "[:eat]";//饭
////	public static final String ee_63 = "[:pig]";//猪头
//	public static final String ee_64 = "[:rose]";//玫瑰
////	public static final String ee_65 = "[:fade]";//凋谢
//	public static final String ee_66 = "[:showlove]";//嘴唇
//	public static final String ee_67 = "[:heart]";//爱心
//	public static final String ee_68 = "[:break]";//心碎
////	public static final String ee_69 = "[:cake]";//蛋糕
////	public static final String ee_70 = "[:li]";//闪电
//	public static final String ee_71 = "[:bome]";//炸弹
////	public static final String ee_72 = "[:kn]";//刀
////	public static final String ee_73 = "[:footb]";//足球
////	public static final String ee_74 = "[:ladybug]";//瓢虫
////	public static final String ee_75 = "[:shit]";//便便
//	public static final String ee_76 = "[:moom]";//月亮
//	public static final String ee_77 = "[:sun]";//太阳
//	public static final String ee_78 = "[:gift]";//礼物
////	public static final String ee_79 = "[:hug]";//拥抱
//	public static final String ee_80 = "[:strong]";//强
//	public static final String ee_81 = "[:weak]";//弱
////	public static final String ee_82 = "[:share]";//握手
//	public static final String ee_83 = "[;v]";//胜利
////	public static final String ee_84 = "[:@)]";//抱拳
////	public static final String ee_85 = "[:jj]";//勾引
//	public static final String ee_86 = "[:@@]";//拳头
////	public static final String ee_87 = "[:bad]";//差劲
////	public static final String ee_88 = "[:lvu]";//爱你
//	public static final String ee_89 = "[:no]";//NO
//	public static final String ee_90 = "[:ok]";//OK

//	public static final String ee_1 = "[::)]";//微笑
//	public static final String ee_2 = "[::~]";//撇嘴
//	public static final String ee_3 = "[::B]";//色
//	public static final String ee_4 = "[::\\]";//发呆
//	public static final String ee_5 = "[:8-)]";//得意
//	public static final String ee_6 = "[::<]";//流泪
//	public static final String ee_7 = "[::$]";//害羞
//	public static final String ee_8 = "[::X]";//闭嘴
//	public static final String ee_9 = "[::Z]";//睡
//
//	public static final String ee_10 = "[::@]";//发怒
//	public static final String ee_11 = "[::P]";//调皮
//	public static final String ee_12 = "[::D]";//呲牙
//	public static final String ee_13 = "[::O]";//惊讶
//	public static final String ee_14 = "[::(]";//难过
//
//	public static final String ee_15 = "[:,@-D]";//愉快
//	public static final String ee_16 = "[::d]";//白眼
//	public static final String ee_17 = "[:,@o]";//傲慢
//
//	public static final String ee_18 = "[:|-)]";//困
//	public static final String ee_19 = "[::!]";//惊恐
//	public static final String ee_20 = "[::L]";//流汗
//	public static final String ee_21 = "[::>]";//憨笑
//	public static final String ee_22 = "[:,@@]";//晕
//	public static final String ee_23 = "[::8]";//疯啦
//	public static final String ee_24 = "[:bye]";//再见
//	public static final String ee_25 = "[:wipe]";//擦汗
//
//	public static final String ee_26 = "[:handclap]";//鼓掌
//	public static final String ee_27 = "[:B-)]";//坏
//	public static final String ee_28 = "[::'|]";//快哭了
//	public static final String ee_29 = "[:x-)]";//阴险
//	public static final String ee_30 = "[:::*]";//亲亲
//	public static final String ee_31 = "[:rose]";//玫瑰
//	public static final String ee_32 = "[:showlove]";//嘴唇
//	public static final String ee_33 = "[:heart]";//爱心
//	public static final String ee_34 = "[:break]";//心碎
//	public static final String ee_35 = "[:bome]";//炸弹
//	public static final String ee_36 = "[:moom]";//月亮
//	public static final String ee_37 = "[:sun]";//太阳
//	public static final String ee_38 = "[:gift]";//礼物
//	public static final String ee_39 = "[:strong]";//强
//	public static final String ee_40 = "[:weak]";//弱
//
//	public static final String ee_41 = "[;v]";//胜利
//	public static final String ee_42 = "[:@@]";//拳头
//	public static final String ee_43 = "[:no]";//NO
//	public static final String ee_44 = "[:ok]";//OK


	public static final String ee_1 = "[微笑]";//
	public static final String ee_2 = "[撇嘴]";//
	public static final String ee_3 = "[色]";//
	public static final String ee_4 = "[发呆]";//
	public static final String ee_5 = "[得意]";//
	public static final String ee_6 = "[流泪]";//
	public static final String ee_7 = "[害羞]";//
	public static final String ee_8 = "[闭嘴]";//
	public static final String ee_9 = "[睡]";//
	public static final String ee_10 = "[大哭]";//
	public static final String ee_11 = "[尴尬]";//
	public static final String ee_12 = "[发怒]";//
	public static final String ee_13 = "[调皮]";//
	public static final String ee_14 = "[呲牙]";//
	public static final String ee_15 = "[惊讶]";//
	public static final String ee_16 = "[难过]";//
	public static final String ee_17 = "[酷]";//
	public static final String ee_18 = "[冷汗]";//
	public static final String ee_19 = "[抓狂]";//
	public static final String ee_20 = "[吐]";//
	public static final String ee_21 = "[偷笑]";//
	public static final String ee_22 = "[愉快]";//
	public static final String ee_23 = "[白眼]";//
	public static final String ee_24 = "[傲慢]";//
	public static final String ee_25 = "[饥饿]";//
	public static final String ee_26 = "[困]";//
	public static final String ee_27 = "[惊恐]";//
	public static final String ee_28 = "[流汗]";//
	public static final String ee_29 = "[憨笑]";//
	public static final String ee_30 = "[悠闲]";//
	public static final String ee_31 = "[奋斗]";//
	public static final String ee_32 = "[咒骂]";//
	public static final String ee_33 = "[疑问]";//
	public static final String ee_34 = "[嘘]";//
	public static final String ee_35 = "[晕]";//
	public static final String ee_36 = "[疯啦]";//
	public static final String ee_37 = "[衰]";//
	public static final String ee_38 = "[骷髅]";//
	public static final String ee_39 = "[敲打]";//
	public static final String ee_40 = "[再见]";//
	public static final String ee_41 = "[擦汗]";//
	public static final String ee_42 = "[抠鼻]";//
	public static final String ee_43 = "[鼓掌]";//
	public static final String ee_44 = "[糗大了]";//
	public static final String ee_45 = "[坏]";//
	public static final String ee_46 = "[左哼哼]";//
	public static final String ee_47 = "[右哼哼]";//
	public static final String ee_48 = "[哈欠]";//
	public static final String ee_49 = "[鄙视]";//
	public static final String ee_50 = "[委屈]";//
	public static final String ee_51 = "[快哭了]";//
	public static final String ee_52 = "[阴险]";//
	public static final String ee_53 = "[亲亲]";//
	public static final String ee_54 = "[吓]";//
	public static final String ee_55 = "[可怜]";//
	public static final String ee_56 = "[菜刀]";//
	public static final String ee_57 = "[西瓜]";//
	public static final String ee_58 = "[啤酒]";//
	public static final String ee_59 = "[篮球]";//
	public static final String ee_60 = "[兵乓]";//
	public static final String ee_61 = "[咖啡]";//
	public static final String ee_62 = "[饭]";//
	public static final String ee_63 = "[猪头]";//
	public static final String ee_64 = "[玫瑰]";//
	public static final String ee_65 = "[凋谢]";//
	public static final String ee_66 = "[嘴唇]";//
	public static final String ee_67 = "[爱心]";//
	public static final String ee_68 = "[心碎]";//
	public static final String ee_69 = "[蛋糕]";//
	public static final String ee_70 = "[闪电]";//
	public static final String ee_71 = "[炸弹]";//
	public static final String ee_72 = "[刀]";//
	public static final String ee_73 = "[足球]";//
	public static final String ee_74 = "[瓢虫]";//
	public static final String ee_75 = "[便便]";//
	public static final String ee_76 = "[月亮]";//
	public static final String ee_77 = "[太阳]";//
	public static final String ee_78 = "[礼物]";//
	public static final String ee_79 = "[拥抱]";//
	public static final String ee_80 = "[强]";//
	public static final String ee_81 = "[弱]";//
	public static final String ee_82 = "[握手]";//
	public static final String ee_83 = "[胜利]";//
	public static final String ee_84 = "[抱拳]";//
	public static final String ee_85 = "[勾引]";//
	public static final String ee_86 = "[拳头]";//
	public static final String ee_87 = "[差劲]";//
	public static final String ee_88 = "[爱你]";//
	public static final String ee_89 = "[NO]";//
	public static final String ee_90 = "[OK]";//

	private static final Factory spannableFactory = Spannable.Factory
	        .getInstance();
	
	private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();
	

	static {
	    EaseEmojicon[] emojicons = EaseDefaultEmojiconDatas.getData();
	    for(int i = 0; i < emojicons.length; i++){
	        addPattern(emojicons[i].getEmojiText(), emojicons[i].getIcon());
	    }
	    EaseEmojiconInfoProvider emojiconInfoProvider = EaseUI.getInstance().getEmojiconInfoProvider();
	    if(emojiconInfoProvider != null && emojiconInfoProvider.getTextEmojiconMapping() != null){
	        for(Entry<String, Object> entry : emojiconInfoProvider.getTextEmojiconMapping().entrySet()){
	            addPattern(entry.getKey(), entry.getValue());
	        }
	    }
	    
	}

	/**
	 * 添加文字表情mapping
	 * @param emojiText emoji文本内容
	 * @param icon 图片资源id或者本地路径
	 */
	public static void addPattern(String emojiText, Object icon){
	    emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
	}
	

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Object value = entry.getValue();
	                if(value instanceof String && !((String) value).startsWith("http")){
	                    File file = new File((String) value);
	                    if(!file.exists() || file.isDirectory()){
	                        return false;
	                    }
	                    spannable.setSpan(new ImageSpan(context, Uri.fromFile(file)),
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }else{
	                    spannable.setSpan(new ImageSpan(context, (Integer)value),
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	            }
	        }
	    }
	    
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}

	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	public static int getSmilesSize(){
        return emoticons.size();
    }
    
	
}
