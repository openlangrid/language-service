WordNetラッパー Ver1.2


概要:

　WordNet 3.0を概念辞書サービスとしてWebサービス化するラッパーです。
　WordNetのアクセスには、JWNL(Java WordNet Library - http://sourceforge.net/projects/jwordnet/)の
最新ツリー(2008/7/30 18:20 JST)にバグを除去するパッチを当てたもの(lib/jwnl-DEVELOPMENT.jar)を使用しています。


利用方法：

　通常のラッパーと同じように配備し、使用してください。


設定方法：

　クラスパスのルート(WEB-INF/classes)にfile_properties.xmlを設置してください。file_properties.xmlの
名称を変更した場合は、wsddの以下のパラメータを設定してください。

<service name="Wordnet"　
	<parameter name="wordnet.propertiesFile" value="file_properties.xml" />
	....
</service>

上記のfile_properties.xmlを変更すれば、任意の名前にすることができます。


概念IDについて:

　下記の形式で概念IDを作成しています。
urn:langrid:wordnet:concept:${品詞}:${オフセット}
WordNetの辞書データが変更されると、古い概念IDが違うものを指したり、そもそも存在しなくなる可能性があります。
WordNetでは、個々の概念に恒久的なIDを振っているわけではないようなので、ラッパー側で一意性を確保することは
できません。


品詞のマッピングについて:

WordNetの品詞:JNWLの品詞:言語グリッドの品詞
NOUN:NOUN:noun
VERB:VERB:verb
ADJECTIVE:ADJECTIVE:adjective
ADVERB:ADVERB:adverb


概念関係のマッピングについて:

WordNetの概念関係:JWNLの概念関係:言語グリッドの概念関係(未完)
hypernym:HYPERNYM:HYPERNYMS
HYPONYM:HYPONYMS
troponym:HYPONYM:HYPONYMS
INSTANCE_HYPERNYM:HYPERNYMS
INSTANCES_HYPONYM:HYPONYMS
MEMBER_MERONYM:MERONYMS
MEMBER_HOLONYM:HOLONYMS
PART_MERONYM:MERONYMS
PART_HOLONYM:HOLONYMS
SUBSTANCE_MERONYM:MERONYMS
SUBSTANCE_HOLONYM:HOLONYMS
ANTONYM:なし



パッチについて：

　net.didion.jwnl.dictionary.FileBackedDictionaryと　net.didion.jwnl.dictionary.file_manager.FileManagerImpl
にパッチが当ててあります。以下、その内容とdiffです。

・FileManagerImpl.readLineWord
末尾になってもnullを返さないので、getMatchingLinePointerで無限ループに陥る。
c == -1でinput.length() == 0の場合nullを返すように

・FileBackedDictionary.FileLookaheadIterator.nextLine
Exceptionをキャッチしていて、想定しない例外が返った場合に原因を特定できない。
JWNLException、IOException、RemoteExceptionのみキャッチするように

・FileBackedDictionary.SubstringIndexFileLookaheadIterator.getNextOffset
親クラスのコンストラクタ実行中にこのメソッドが呼ばれるため、
_substringがnullの場合があり、getFileManager().getMatchingLinePointerを
呼び出すとNullPointerExceptionが発生する。_sustringがnullの場合は親クラスの
getNextOffsetメソッドを呼び出す。
また、現在の行をスキップしないため、無限ループに陥る。一旦getNextLinePonterを
呼び出して行をスキップし、その後getMatchingLinePointerを呼び出す。

・FileManagerImpl.getMatchingLinePointer
readLineWordだけでは次行に進まないので、無駄な語句のマッチングが行われる。
語句がマッチしなかった場合はskipLine呼び出しを行う。

・FileBackedDictionary.SubstringIndexFileLookaheadIterator.SubstringIndexFileLookaheadIterator
nextOffset呼び出し一回では、最初の語句にポインタが移動し、現在行は最初の行
になるので、次に語句を取得すると最初の行が取得される。
nextLineを続けて呼び出しておくことで、2番目の語句にポインタが移動し、
最初の語句が取得できる状態になる。

・FileManagerImpl.skipLine
ファイル末尾(c == -1)の場合もポインタを1つ戻すので、skipLine呼び出し後は
ファイル末尾を検出できない。
c==-1の場合はseekせずreturnする。

・AbstractPrincetonFileDictionaryElementFactory.createSynset
Wordのindexとして、同義語配列の順番を設定している。WordNet Onlineでは、その同義語の
インデックスそのもの(データファイル中に定義)を利用しているため、これに合わせる。
tokenizer.nextHexInt()の戻り値を捨てているが、これをindexとして利用。
また、語に含まれる"(p)"を除去していない。WordNet Onlineでは除去しているので、それに
合わせる。


　Index: C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/dictionary/FileBackedDictionary.java
===================================================================
--- C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/dictionary/FileBackedDictionary.java	(revision 26)
+++ C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/dictionary/FileBackedDictionary.java	(working copy)
@@ -5,6 +5,7 @@
 package net.didion.jwnl.dictionary;
 
 import java.io.IOException;
+import java.rmi.RemoteException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
@@ -353,7 +354,9 @@
                     nextOffset();
                     return;
                 }
-            } catch (Exception ex) {
+            } catch (RemoteException ex) {
+            } catch (IOException ex) {
+            } catch (JWNLException ex) {
             }
             _more = false;
 		}
@@ -394,11 +397,16 @@
 			super(pos);
 			_substring = substring;
 			nextOffset();
+			nextLine();
 		}
 
 		protected long getNextOffset(long currentOffset) throws JWNLException {
+			if(_substring == null){
+				return super.getNextOffset(currentOffset);
+			}
 			try {
-				return getFileManager().getMatchingLinePointer(_pos, DictionaryFileType.INDEX, currentOffset, _substring);
+				long offset = getFileManager().getNextLinePointer(_pos, DictionaryFileType.INDEX, currentOffset);
+				return getFileManager().getMatchingLinePointer(_pos, DictionaryFileType.INDEX, offset, _substring);
 			} catch (IOException ex) {
 				throw new JWNLException("DICTIONARY_EXCEPTION_008", new Object[]{_pos, _fileType}, ex);
 			}
@@ -404,7 +412,7 @@
 			}
 		}
 	}
-	
+
 	/**
 	 * {@inheritDoc}
 	 */

Index: C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/dictionary/file_manager/FileManagerImpl.java
===================================================================
--- C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/dictionary/file_manager/FileManagerImpl.java	(revision 26)
+++ C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/dictionary/file_manager/FileManagerImpl.java	(working copy)
@@ -136,6 +136,7 @@
 	private void skipLine(RandomAccessDictionaryFile file) throws IOException {
 		int c;
 		while (((c = file.read()) != -1) && c != '\n' && c != '\r');
+		if(c == -1) return;
 		c = file.read();
 		if (c != '\n' && c != '\r') {
 			file.seek(file.getFilePointer()-1);
@@ -172,6 +173,7 @@
 		while (((c = file.read()) != -1) && c != '\n' && c != '\r' && c != ' ') {
 			input.append((char) c);
 		}
+		if(c == -1 && input.length() == 0) return null;
 		return input.toString();
 	}
 
@@ -203,11 +205,13 @@
 			file.seek(offset);
 			do {
 				String line = readLineWord(file);
-				long nextOffset = file.getFilePointer();
 				if (line == null) return -1;
-				file.setNextLineOffset(offset, nextOffset);
-				if (line.indexOf(substring) >= 0) return offset;
-				offset = nextOffset;
+				if (line.indexOf(substring) >= 0){
+					file.setNextLineOffset(offset, file.getFilePointer());
+					return offset;
+				}
+				skipLine(file);
+				offset = file.getFilePointer();
 			} while (true);
 		}
 	}

Index: C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/princeton/data/AbstractPrincetonFileDictionaryElementFactory.java
===================================================================
--- C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/princeton/data/AbstractPrincetonFileDictionaryElementFactory.java	(revision 26)
+++ C:/home/takao/workspace_wrappers/jwnl/src/net/didion/jwnl/princeton/data/AbstractPrincetonFileDictionaryElementFactory.java	(working copy)
@@ -79,11 +79,12 @@
         for (int i = 0; i < wordCount; i++) {
             String lemma = tokenizer.nextToken();
             
-            tokenizer.nextHexInt(); // lex id
+            lemma = lemma.split("\\(")[0];
+            int index = tokenizer.nextHexInt(); // lex id
             
           
             
-            words[i] = createWord(proxy, i, lemma);
+            words[i] = createWord(proxy, index, lemma);
             
         }


2008/8/1
中口
