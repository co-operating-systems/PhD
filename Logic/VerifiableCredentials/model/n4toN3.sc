import org.apache.jen3.rdf.model.Model
import org.apache.jen3.rdf.model.ModelFactory
import org.apache.jen3.sys.JenaSystem
import org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF
import org.apache.jen3.n3.N3ModelSpec
import java.io.FileInputStream
import org.apache.jen3.n3.io.N3JenaWriterFull

val spec = N3ModelSpec.get(N3_MEM_FP_INF)
val m = ModelFactory.createN3Model(spec)
m.read(new FileInputStream(args(0)), null)

m.setNsPrefix("sec","https://w3id.org/security#")
m.setNsPrefix("cred","https://www.w3.org/2018/credentials#")
//m.setNsPrefix("educred","http://example.edu/credentials/")
m.setNsPrefix("eg","http://example.org/examples#")
m.setNsPrefix("sch","http://schema.org/")
val wr = new org.apache.jen3.n3.io.N3JenaWriterFull(true)
wr.write(m, System.out, null)

