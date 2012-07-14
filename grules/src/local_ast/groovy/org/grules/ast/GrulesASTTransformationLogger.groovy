package org.grules.ast

import org.codehaus.groovy.control.io.NullWriter

/**
 * A logger for grules script transformations.
 */
class GrulesASTTransformationLogger implements Closeable {
	
	private static final String LOG_DIR = '/tmp/grules'
	protected final Writer writer
		
	GrulesASTTransformationLogger(String filename) {
		try {
			File logDir = new File(LOG_DIR)
			logDir.mkdirs()
			File logFile = new File("$LOG_DIR/${filename}.log")
			logFile.createNewFile()
			writer = new BufferedWriter(new FileWriter(logFile))
		} catch (IOException e) {
		  writer = NullWriter.DEFAULT
		}
	}
	
	void write(String message) {
		String sanitizedMessage = message.replaceAll(org.codehaus.groovy.ast.ClassNode.package.name + '.', '')
		sanitizedMessage = sanitizedMessage.replaceAll('@\\w+', '')
		writer.write(sanitizedMessage + '\n\n')
	}
	
	void close() {
		writer.close()
	}	
}